package illarion.client.util.account.services;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import illarion.client.IllaClient;
import illarion.client.Servers;
import illarion.client.graphics.AvatarClothManager;
import illarion.client.graphics.AvatarEntity;
import illarion.client.gui.EntityRenderImage;
import illarion.client.resources.CharacterFactory;
import illarion.client.util.account.AccountSystem;
import illarion.client.util.account.response.CharacterGetResponse;
import illarion.common.graphics.CharAnimations;
import illarion.common.types.AvatarId;
import illarion.common.types.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.illarion.engine.Option;
import org.illarion.engine.ui.CharacterSelectionData;
import org.illarion.engine.ui.DynamicUiContent;
import org.illarion.engine.ui.LoginData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class LoginService {
    private static final Logger LOGGER = LogManager.getLogger();
    private final AccountSystem accountSystem;

    private final ExecutorService requestThreadPool;

    public LoginService(AccountSystem accountSystem) {
        this.accountSystem = accountSystem;
        requestThreadPool = Executors.newCachedThreadPool();
    }

    public void issueLogin(LoginData loginData, FutureCallback<CharacterSelectionData[]> loginCallback) {
        var usedServer = Arrays.stream(Servers.values())
                .filter(server -> server.getServerName().equals(loginData.server))
                .findFirst()
                .orElse(Servers.Illarionserver);

        // TODO: Testserver is used as a placeholder for LocalServer until merged
        if (usedServer == Servers.Testserver ||
                (usedServer == Servers.Customserver
                        && !IllaClient.getConfig().getBoolean(Option.customServerAccountSystem))) {
            LOGGER.debug("AccountSystem not active, executing a direct login");
            return;
        }

        var endpoint = usedServer == Servers.Customserver
                ? "https://" + usedServer.getServerHost() + "/app.php"
                : AccountSystem.OFFICIAL_ENDPOINT;

        accountSystem.setAuthentication(loginData);
        accountSystem.setEndpoint(endpoint);

        var accountInformation = accountSystem.getAccountInformation();

        var characterInformation = Futures.transformAsync(accountInformation, (result) -> {
            if (result == null) {
                throw new RuntimeException("Request returned <null> value for account information");
            }

            var serverCharacterList = result.getChars();

            var charInformationRequests = serverCharacterList
                    .stream()
                    .filter(server -> server.getId().equals(usedServer.getServerName()))
                    .findFirst()
                    .map(server -> server.getList()
                            .stream()
                            .map(character ->
                                    accountSystem.getCharacterInformation(server.getId(), character.getCharId()))
                            .collect(Collectors.toList()))
                    .orElse(List.of());

            return Futures.successfulAsList(charInformationRequests);
        }, requestThreadPool);

        var charactersLoaded = Futures.transformAsync(characterInformation, (result) -> {
            if (result == null) {
                throw new RuntimeException("Request returned <null> value for character information");
            }

            return Futures.immediateFuture(result.stream()
                    .filter(Objects::nonNull)
                    .map(character -> new CharacterSelectionData(
                            character.getId(),
                            character.getName(),
                            buildCharacterRenderable(character)))
                    .toArray(CharacterSelectionData[]::new));
        }, requestThreadPool);

        Futures.addCallback(charactersLoaded, loginCallback, requestThreadPool);
    }

    private static DynamicUiContent buildCharacterRenderable(CharacterGetResponse character) {
        var id = new AvatarId(character.getRace(), character.getRaceType(), Direction.West, CharAnimations.STAND);
        var template = CharacterFactory.getInstance().getTemplate(id.getAvatarId());
        var avatarEntity = new AvatarEntity(template, true);

        var paperDoll = character.getPaperDoll();
        avatarEntity.setClothItem(AvatarClothManager.AvatarClothGroup.Hair, paperDoll.getHairId());
        avatarEntity.setClothItem(AvatarClothManager.AvatarClothGroup.Beard, paperDoll.getBeardId());
        avatarEntity.changeClothColor(AvatarClothManager.AvatarClothGroup.Hair, paperDoll.getHairColour().getColour());
        avatarEntity.changeClothColor(AvatarClothManager.AvatarClothGroup.Beard, paperDoll.getHairColour().getColour());
        avatarEntity.changeBaseColor(paperDoll.getSkinColour().getColour());

        for (var item : character.getItems()) {
            var group = AvatarClothManager.AvatarClothGroup.getFromPositionNumber(item.getPosition());

            if (group == null) {
                continue;
            }

            if (item.getId() == 0) {
                avatarEntity.removeClothItem(group);
                continue;
            }

            avatarEntity.setClothItem(group, item.getId());
        }

        return new EntityRenderImage(avatarEntity);
    }
}
