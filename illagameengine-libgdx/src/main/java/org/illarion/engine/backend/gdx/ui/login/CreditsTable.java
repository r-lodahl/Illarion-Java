package org.illarion.engine.backend.gdx.ui.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import java.util.ArrayList;
import java.util.List;

public class CreditsTable extends Table {
    private Skin skin;
    private List<VerticalGroup> creditSequence;
    private int currentlyShownCredit;

    private final static float TIME_PER_CREDIT = 1f;
    private float timeWaited;

    public CreditsTable(Skin skinIn) {
        skin = skinIn;
        setFillParent(true);

        /* Categories */
        CreditsCategory projectManager = new CreditsCategory("Projekt Manager", "Projektleiter");
        CreditsCategory contentLead = new CreditsCategory("Lead Content Developer", "Leitender Entwickler für Spieleinhalte");
        CreditsCategory clientLead = new CreditsCategory("Lead Client Developer", "Leitender Entwickler für den Client");
        CreditsCategory mapLead = new CreditsCategory("Lead Mapper", "Leitender Entwickler für die Karte");
        CreditsCategory serverLead = new CreditsCategory("Lead Server Developer", "Leitender Entwickler für den Server");

        CreditsCategory gameplayContributor = new CreditsCategory("Gameplay", "Spielinhalte");
        CreditsCategory clientContributor = new CreditsCategory("Client", "Client");
        CreditsCategory graphicContributor = new CreditsCategory("Graphics", "Grafiken");
        CreditsCategory mapContributor = new CreditsCategory("Maps", "Karten");
        CreditsCategory musicContributor = new CreditsCategory("Music & Sounds", "Musik und Ton");
        CreditsCategory editorContributor = new CreditsCategory("Game-Editors", "Spieleditoren");
        CreditsCategory serverContributor = new CreditsCategory("Server", "Server");
        CreditsCategory websiteContributor = new CreditsCategory("Website", "Webseite");

        CreditsCategory gameMaster = new CreditsCategory("Game Master", "Spielleiter");
        CreditsCategory communityManager = new CreditsCategory("Community Manager", "Community-Manager");

        CreditsCategory additionalThanks = new CreditsCategory("Additional Supporter", "Weitere Helfer");
        CreditsCategory specialThanks = new CreditsCategory("Special Thanks", "Besonderer Dank");

        CreditsCategory presentedBy = new CreditsCategory("Presented By", "Präsentiert von");

        /* Create and Assign Persons */
        assignPersonToCategories("Lennart \"Estralis\" Lacroix", projectManager, contentLead, websiteContributor, gameplayContributor);
        assignPersonToCategories("Andreas \"Vilarion\" Grob", serverLead, gameplayContributor, clientContributor, websiteContributor, editorContributor, serverContributor);
        assignPersonToCategories("\"Jupiter\"", gameplayContributor);
        assignPersonToCategories("\"CJK\"", clientContributor);
        assignPersonToCategories("Robert \"Seajiha\" L.", gameplayContributor, clientContributor);
        assignPersonToCategories("\"Grim\"", graphicContributor);
        assignPersonToCategories("\"Snus-Mumrik\"", gameplayContributor);
        assignPersonToCategories("\"Evie\"", mapLead, mapContributor);
        assignPersonToCategories("\"Slightly\"", gameMaster, mapContributor);
        assignPersonToCategories("\"Drakon Gerwulf\"", graphicContributor);
        assignPersonToCategories("Martin \"Nitram\" Karing", gameplayContributor, clientContributor, websiteContributor, editorContributor, serverContributor);
        assignPersonToCategories("Allison \"Dantagon\" Geraci", gameplayContributor);
        assignPersonToCategories("\"Achae Eanstry\"", communityManager, graphicContributor);
        assignPersonToCategories("Martin Polak", gameplayContributor, clientContributor, graphicContributor, websiteContributor, editorContributor);
        assignPersonToCategories("Oganalp Canatan", musicContributor);
        assignPersonToCategories("Marvin Kopp", musicContributor);
        assignPersonToCategories("\"Zot\"", gameplayContributor, graphicContributor, mapContributor);
        assignPersonToCategories("Andreas \"Caldarion\" Gahr", gameplayContributor);
        assignPersonToCategories("\"Ardian\"", gameplayContributor);
        assignPersonToCategories("Esther \"Kadiya\" Sense", gameplayContributor, graphicContributor, websiteContributor);
        assignPersonToCategories("Jan Mattner", gameplayContributor);
        assignPersonToCategories("Wolfgang Müller", gameplayContributor);
        assignPersonToCategories("Thomas Messerschmidt", gameplayContributor);
        assignPersonToCategories("\"Faladron\"", gameplayContributor);
        assignPersonToCategories("Henry Mill", gameplayContributor, mapContributor);
        assignPersonToCategories("Lisa Maletzki", gameplayContributor);
        assignPersonToCategories("Marion \"Miriam\" Herstell", gameplayContributor, clientContributor);
        assignPersonToCategories("\"Vitoria\"", gameplayContributor);
        assignPersonToCategories("Nikolaus \"Nalcaryos\" Tauß", gameplayContributor);
        assignPersonToCategories("\"Grokk\"", gameplayContributor);
        assignPersonToCategories("Kawan \"Regallo\" Baxter", gameplayContributor);
        assignPersonToCategories("Alex \"Flux\" Rose", gameplayContributor);
        assignPersonToCategories("\"Llama\"", gameplayContributor);
        assignPersonToCategories("Domininc \"Dyluck\" W.", gameplayContributor);
        assignPersonToCategories("Martin \"Skamato\" Großmann", gameplayContributor);
        assignPersonToCategories("\"Tiim\"", clientContributor, editorContributor);
        assignPersonToCategories("Frederik K.", clientContributor, editorContributor);
        assignPersonToCategories("\"Smjert\"", clientContributor);
        assignPersonToCategories("\"Samaras\"", graphicContributor);
        assignPersonToCategories("Karl Salameh", graphicContributor);
        assignPersonToCategories("Matt \"Raelith\" Hollier", websiteContributor);
        assignPersonToCategories("Jaime \"Quinasa\" Hughes", additionalThanks);
        assignPersonToCategories("H.-Robert \"Damien\" Matthes", additionalThanks);
        assignPersonToCategories("\"Rakaya\"", additionalThanks);
        assignPersonToCategories("Larissa \"Soraja\" Falkenbach", additionalThanks);
        assignPersonToCategories("\"Alrik\"", additionalThanks);
        assignPersonToCategories("\"Katharina\"", additionalThanks);
        assignPersonToCategories("Mike \"Salathe\" Hudak", additionalThanks);
        assignPersonToCategories("\"PurpleMonkeys\"", additionalThanks);
        assignPersonToCategories("\"GolfLima\"", additionalThanks);
        assignPersonToCategories("Victor \"Vigalf\" Becker", additionalThanks);
        assignPersonToCategories("Cindy \"Elynah\" Ludwig", additionalThanks);
        assignPersonToCategories("\"Mesha\"", mapContributor);
        assignPersonToCategories("Oliver Herzog", mapContributor);
        assignPersonToCategories("\"Quirkily\"", gameplayContributor, mapContributor);
        assignPersonToCategories("\"Banduk\"", gameplayContributor);
        assignPersonToCategories("\"Arien Edhel\"", specialThanks);
        assignPersonToCategories("Jan \"Alatar\" Falke", specialThanks);
        assignPersonToCategories("\"Aragon ben Galwan\"", specialThanks);
        assignPersonToCategories("void256", specialThanks);
        assignPersonToCategories("Illarion e.V.", presentedBy);

        /* Layout */
        creditSequence = new ArrayList<>(18);
        creditSequence.add(categoryToTable(projectManager));
        creditSequence.add(categoryToTable(contentLead));
        creditSequence.add(categoryToTable(serverLead));
        creditSequence.add(categoryToTable(mapLead));
        creditSequence.add(categoryToTable(gameMaster));
        creditSequence.add(categoryToTable(communityManager));
        creditSequence.add(categoryToTable(gameplayContributor));
        creditSequence.add(categoryToTable(clientContributor));
        creditSequence.add(categoryToTable(serverContributor));
        creditSequence.add(categoryToTable(graphicContributor));
        creditSequence.add(categoryToTable(mapContributor));
        creditSequence.add(categoryToTable(musicContributor));
        creditSequence.add(categoryToTable(websiteContributor));
        creditSequence.add(categoryToTable(editorContributor));
        creditSequence.add(categoryToTable(additionalThanks));
        creditSequence.add(categoryToTable(specialThanks));
        creditSequence.add(categoryToTable(presentedBy));

        currentlyShownCredit = 0;
        timeWaited = 0;
        add(creditSequence.get(currentlyShownCredit));
    }

    private void assignPersonToCategories(String person, CreditsCategory... categories) {
        for (CreditsCategory category : categories) {
            category.addContributor(person);
        }
    }

    private VerticalGroup categoryToTable(CreditsCategory category) {
        VerticalGroup categoryTable = new VerticalGroup();
        categoryTable.addActor(new Label(category.getNameEn(), skin));

        for (String contributor : category.getContributors()) {
            categoryTable.addActor(new Label(contributor, skin));
        }

        return categoryTable;
    }

    private boolean hasNextCredit() {
        return currentlyShownCredit + 1 < creditSequence.size();
    }

    private void showNextCredit() {
        removeActor(creditSequence.get(currentlyShownCredit));
        currentlyShownCredit++;
        add(creditSequence.get(currentlyShownCredit));
    }

    public boolean cycleCredits() {
        timeWaited += Gdx.graphics.getDeltaTime();

        if (timeWaited < TIME_PER_CREDIT) {
            return false;
        }

        if (!hasNextCredit()) {
            return true;
        }

        showNextCredit();
        timeWaited = 0f;

        return false;
    }
}
