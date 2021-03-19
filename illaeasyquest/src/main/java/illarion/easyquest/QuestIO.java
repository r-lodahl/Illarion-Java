/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.easyquest;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;
import illarion.easyquest.quest.*;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * This is the input/output class for the quests. It handles loading and saving quest graphs. Also it handles
 * creating the LUA quest files.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class QuestIO {
    /**
     * The character sets that will be tried to load the file. One by one. The first one in the list is
     * {@link #CHARSET} as this one is the most likely one to be the one used.
     */
    @NotNull
    private static final Collection<Charset> CHARSETS;

    /**
     * The char set that is used by default to load and save the data.
     */
    public static final Charset CHARSET;

    static {
        CHARSET = StandardCharsets.ISO_8859_1;
        CHARSETS = new ArrayList<>();
        CHARSETS.add(CHARSET);
        CHARSETS.addAll(Charset.availableCharsets().values());

        mxCodecRegistry.register(new mxObjectCodec(new Handler()));
        mxCodecRegistry.addPackage(Handler.class.getPackage().getName());
        mxCodecRegistry.register(new mxObjectCodec(new Status()));
        mxCodecRegistry.addPackage(Status.class.getPackage().getName());
        mxCodecRegistry.register(new mxObjectCodec(new Trigger()));
        mxCodecRegistry.addPackage(Trigger.class.getPackage().getName());
        mxCodecRegistry.register(new mxObjectCodec(new Position()));
        mxCodecRegistry.addPackage(Position.class.getPackage().getName());
    }

    private QuestIO() {
    }

    /**
     * Load the graph model of a quest from a file. This is the quest data as its handled internally by the easyQuest
     * editor.
     *
     * @param file the file that is used as data source
     * @return the graph model instance containing the data of the file
     * @throws IOException in case reading the model from the file fails for any reason
     */
    @NotNull
    public static mxIGraphModel loadGraphModel(@NotNull Path file) throws IOException {
        if (!Files.isReadable(file)) {
            throw new IOException("Can't read the required file.");
        }
        IOException firstException = null;
        for (Charset charset : CHARSETS) {
            try (Reader reader = Files.newBufferedReader(file, charset)) {
                return loadGraphModel(reader);
            } catch (IOException e) {
                if (firstException == null) {
                    firstException = e;
                }
            }
        }
        throw firstException;
    }

    @NotNull
    public static mxIGraphModel loadGraphModel(@NotNull Reader reader) throws IOException {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            Document document = docBuilder.parse(new InputSource(reader));
            mxCodec codec = new mxCodec(document);
            mxIGraphModel model = new mxGraphModel();
            codec.decode(document.getDocumentElement(), model);
            return model;
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException(e);
        }
    }

    /**
     * Save a graph model to the file system.
     *
     * @param model the model to store to the file system
     * @param target tje target in the file system that will receive the data
     * @throws IOException in case saving the file fails
     */
    public static void saveGraphModel(@NotNull mxIGraphModel model, @NotNull Path target)
            throws IOException {
        if (!Files.isWritable(target)) {
            throw new IOException("Can't write the required file.");
        }
        mxCodec codec = new mxCodec();
        Node node = codec.encode(model);

        if (node == null) {
            throw new IOException("Model can't be encoded to XML.");
        }

        try (Writer writer = Files.newBufferedWriter(target, CHARSET)) {
            Transformer tf = TransformerFactory.newInstance().newTransformer();

            tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            tf.setOutputProperty(OutputKeys.ENCODING, CHARSET.name());

            tf.transform(new DOMSource(node), new StreamResult(writer));
            writer.flush();
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }

    /**
     * Export a quest to its lua files.
     *
     * @param model the quest model
     * @param rootDirectory the directory to store the root directory in
     * @throws IOException in case anything goes wrong
     */
    public static void exportQuest(@NotNull mxIGraphModel model, @NotNull Path rootDirectory)
            throws IOException {
        if (Files.isDirectory(rootDirectory)) {
            Files.walkFileTree(rootDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc == null) {
                        Files.delete(dir);
                        return CONTINUE;
                    } else {
                        throw exc;
                    }
                }
            });
        }
        Files.createDirectories(rootDirectory);

        String questName = rootDirectory.getName(rootDirectory.getNameCount() - 1).toString();

        mxICell root = (mxCell) model.getRoot();
        Object idNode = root.getValue();
        int questID = -1;
        if (idNode != null) {
            try {
                questID = Integer.parseInt(idNode.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        if (questID == -1) {
            throw new IOException("Required quest ID is not set.");
        }

        Path questMainFile = rootDirectory.resolve("quest.txt");

        if (Files.exists(questMainFile)) {
            Files.delete(questMainFile);
        }

        mxGraph graph = new mxGraph(model);
        Object[] edges = graph.getChildEdges(graph.getDefaultParent());

        for (int i = 0; i < edges.length; i++) {
            mxCell edge = (mxCell) edges[i];
            Trigger trigger = (Trigger) edge.getValue();
            TriggerTemplate template = TriggerTemplates.getInstance().getTemplate(trigger.getType());

            String scriptName = "trigger" + (i + 1);
            mxICell source = edge.getSource();
            mxICell target = edge.getTarget();
            Status sourceState = (Status) source.getValue();
            Status targetState = (Status) target.getValue();
            String sourceId = sourceState.isStart() ? "0" : source.getId();
            String targetId = targetState.isStart() ? "0" : target.getId();
            Object[] parameters = trigger.getParameters();
            Handler[] handlers = targetState.getHandlers();
            Collection<String> handlerTypes = new HashSet<>();
            Condition[] conditions = trigger.getConditions();

            StringBuilder handlerCode = new StringBuilder();
            if (handlers != null) {
                for (Handler handler : handlers) {
                    String type = handler.getType();
                    Object[] handlerParameters = handler.getParameters();
                    HandlerTemplate handlerTemplate = HandlerTemplates.getInstance().getTemplate(type);
                    int playerIndex = handlerTemplate.getPlayerIndex();

                    handlerTypes.add(type);

                    handlerCode.append("    handler.").append(type.toLowerCase()).append('.').append(type).append('(');
                    if (handlerParameters.length > 0) {
                        if (playerIndex == 0) {
                            handlerCode.append("PLAYER, ");
                        }
                        handlerCode.append(exportParameter(handlerParameters[0],
                                                           handlerTemplate.getParameter(0).getType()));

                        for (int j = 1; j < handlerParameters.length; ++j) {
                            if (playerIndex == j) {
                                handlerCode.append(", PLAYER");
                            }
                            handlerCode.append(", ").append(exportParameter(handlerParameters[j],
                                                                            handlerTemplate.getParameter(j).getType()));
                        }
                    }
                    handlerCode.append("):execute()\n");
                }
            }

            StringBuilder conditionCode = new StringBuilder();
            if (conditions != null) {
                for (Condition condition : conditions) {
                    String type = condition.getType();
                    Object[] conditionParameters = condition.getParameters();
                    ConditionTemplate conditionTemplate = ConditionTemplates.getInstance().getTemplate(type);
                    String conditionString = conditionTemplate.getCondition();
                    if (conditionString != null) {
                        for (int j = 0; j < conditionParameters.length; ++j) {
                            Object param = conditionParameters[j];
                            String paramName = conditionTemplate.getParameter(j).getName();
                            String paramType = conditionTemplate.getParameter(j).getType();
                            String operator = null;
                            String value = null;
                            if ("INTEGERRELATION".equals(paramType)) {
                                IntegerRelation ir = (IntegerRelation) param;
                                value = String.valueOf(ir.getInteger());
                                operator = ir.getRelation().toLua();
                            }
                            if (operator != null) {
                                conditionString = conditionString.replaceAll("OPERATOR_" + j, operator)
                                        .replaceAll(paramName, value);
                            }
                        }
                    }
                    if (conditionCode.length() > 0) {
                        conditionCode.append("   and ");
                    }
                    conditionCode.append(conditionString).append('\n');
                }
            }
            if (conditionCode.length() == 0) {
                conditionCode.append("true\n");
            }

            try (BufferedWriter writer = Files.newBufferedWriter(rootDirectory.resolve(scriptName + ".lua"), CHARSET)) {
                for (String type : handlerTypes) {
                    writer.write("require(\"handler.");
                    writer.write(type.toLowerCase());
                    writer.write("\"}");
                    writer.newLine();
                }
                String header = template.getHeader();
                if (header != null) {
                    writer.write(header);
                    writer.newLine();
                }

                writer.write("module(\"questsystem.");
                writer.write(questName);
                writer.write('.');
                writer.write(scriptName);
                writer.write("\", package.seeall)");
                writer.newLine();

                writer.write("local QUEST_NUMBER = ");
                writer.write(Integer.toString(questID));
                writer.newLine();

                writer.write("local PRECONDITION_QUESTSTATE = ");
                writer.write(sourceId);
                writer.newLine();

                writer.write("local POSTCONDITION_QUESTSTATE = ");
                writer.write(targetId);
                writer.newLine();

                int paramCount = template.size();
                if (parameters == null || paramCount != parameters.length) {
                    throw new IOException("Required parameters are not present.");
                }
                for (int j = 0; j < template.size(); ++j) {
                    writer.write("local ");
                    writer.write(template.getParameter(j).getName());
                    writer.write(" = ");
                    writer.write(exportParameter(parameters[j], template.getParameter(j).getType()));
                    writer.newLine();
                }

                String body = template.getBody();
                if (body != null) {
                    writer.write(body);
                    writer.newLine();
                }
                writer.write("function HANDLER(PLAYER)");
                writer.newLine();
                writer.write(handlerCode.toString());
                writer.write("end");
                writer.newLine();

                writer.write("function ADDITIONALCONDITIONS(PLAYER)");
                writer.newLine();
                writer.write("return ");
                writer.write(conditionCode.toString());
                writer.write("end");

                writer.flush();
            }

            try (BufferedWriter writer = Files
                    .newBufferedWriter(rootDirectory.resolve("quest.txt"), CHARSET, APPEND, CREATE)) {
                String cat = template.getCategory();
                TemplateParameter id = template.getId();
                String type = id == null ? null : id.getType();
                String entryPoint = template.getEntryPoint();

                if (cat == null || type == null || entryPoint == null) {
                    throw new IOException("Template appears to be incomplete.");
                }

                writer.write(template.getCategory());
                writer.write(',');
                writer.write(exportId(trigger.getObjectId(), template.getId().getType()));
                writer.write(',');
                writer.write(template.getEntryPoint());
                writer.write(',');
                writer.write(scriptName);
                writer.newLine();
                writer.flush();
            }
        }
    }

    private static String exportId(Object parameter, String type) {
        if ("POSITION".equals(type)) {
            Position p = (Position) parameter;
            return p.getX() + "," + p.getY() + ',' + p.getZ();
        }
        if ("INTEGER".equals(type)) {
            if (parameter instanceof Long) {
                Long n = (Long) parameter;
                return n.toString();
            }
            return parameter.toString();
        }
        return "TYPE NOT SUPPORTED";
    }

    private static String exportParameter(@NotNull Object parameter, @NotNull String type) {
        if ("TEXT".equals(type)) {
            String s = (String) parameter;
            return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
        }
        if ("POSITION".equals(type)) {
            Position p = (Position) parameter;
            return "position(" + p.getX() + ", " + p.getY() + ", " + p.getZ() + ')';
        }
        if ("INTEGER".equals(type)) {
            if (parameter instanceof Long) {
                Long n = (Long) parameter;
                return n.toString();
            }
            return parameter.toString();
        }
        return "TYPE NOT SUPPORTED";
    }
}
