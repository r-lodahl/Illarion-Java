package illarion.client.util.account.services;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import illarion.client.graphics.AvatarClothManager;
import illarion.client.graphics.AvatarEntity;
import illarion.client.gui.EntityRenderImage;
import illarion.client.resources.CharacterFactory;
import illarion.client.util.account.AccountSystem;
import illarion.client.util.account.response.CharacterGetResponse;
import illarion.common.graphics.CharAnimations;
import illarion.common.types.AvatarId;
import illarion.common.types.Direction;
import org.illarion.engine.ui.DynamicUiContent;
import org.illarion.engine.ui.login.CharacterSelectionData;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class LoginService {
    private final AccountSystem accountSystem;
    private final ExecutorService executor;

    public LoginService(AccountSystem accountSystem, ExecutorService executor) {
        this.accountSystem = accountSystem;
        this.executor = executor;
    }

    public ListenableFuture<CharacterSelectionData[]> getAccountCharacterList() {
        var accountInformation = accountSystem.getAccountInformation();

        var characterInformation = Futures.transformAsync(accountInformation, (result) -> {
            if (result == null) {
                throw new RuntimeException("Request returned <null> value for account information");
            }

            var serverCharacterList = result.getChars();

            var charInformationRequests = serverCharacterList
                    .stream()
                    .filter(server -> server.getId().equals(accountSystem.getCurrentServer().getServerName()))
                    .findFirst()
                    .map(server -> server.getList()
                            .stream()
                            .map(character ->
                                    accountSystem.getCharacterInformation(server.getId(), character.getCharId()))
                            .collect(Collectors.toList()))
                    .orElse(List.of());

            return Futures.successfulAsList(charInformationRequests);
        }, executor);

        return Futures.transform(characterInformation, (result) -> {
            if (result == null) {
                throw new RuntimeException("Request returned <null> value for character information");
            }

            return result.stream()
                    .filter(Objects::nonNull)
                    .map(character -> new CharacterSelectionData(
                            character.getId(),
                            character.getName(),
                            buildCharacterRenderable(character)))
                    .toArray(CharacterSelectionData[]::new);
        }, executor);
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
