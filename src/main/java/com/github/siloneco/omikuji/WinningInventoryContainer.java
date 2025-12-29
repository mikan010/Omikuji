package com.github.siloneco.omikuji;

import com.github.siloneco.omikuji.utility.Chat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WinningInventoryContainer {

    private final HashMap<String, Inventory> invMap = new HashMap<>();
    private final List<String> alreadyUsedIDs = new ArrayList<>();
    private final HashMap<String, String> publicToSecretIdMap = new HashMap<>();

    private final HashMap<String, WinningsMeta> metaMap = new HashMap<>();

    public record WinningsMeta(
            String publicId,
            String secretId,
            UUID drawerUuid,
            String drawerName,
            String resultId,
            String resultTitle,
            List<ItemStack> itemsSnapshot,
            long createdAtMillis
    ) {}

    public OmikujiWinningInventoryID createInventoryWithID(OmikujiResult result) {
        return createInventoryWithID(result, null);
    }

    public OmikujiWinningInventoryID createInventoryWithID(OmikujiResult result, Player drawer) {
        if (result.getItems().isEmpty()) {
            return null;
        }

        OmikujiWinningInventoryID id = new OmikujiWinningInventoryID(issueID(), issueID());

        int size = 9 * ((int) ((double) result.getItems().size() / 9d) + 1);
        Inventory inv = Bukkit.createInventory(null, size, Chat.f("&cOmikuji Winnings &7- &8{0}", id.getPublicID()));

        for (ItemStack item : result.getItems()) {
            item = item.clone();
            inv.addItem(item);
        }
        invMap.put(id.getSecretID(), inv);
        publicToSecretIdMap.put(id.getPublicID(), id.getSecretID());

        List<ItemStack> snapshot = result.getItems().stream()
                .filter(Objects::nonNull)
                .map(ItemStack::clone)
                .toList();

        metaMap.put(id.getSecretID(), new WinningsMeta(
                id.getPublicID(),
                id.getSecretID(),
                drawer != null ? drawer.getUniqueId() : null,
                drawer != null ? drawer.getName() : null,
                result.getId(),
                result.getDisplayTitle(),
                snapshot,
                System.currentTimeMillis()
        ));

        return id;
    }

    public Inventory getInventory(String secretId) {
        return invMap.getOrDefault(secretId, null);
    }

    public WinningsMeta getMeta(String secretId) {
        return metaMap.getOrDefault(secretId, null);
    }

    public void disposeInventory(String secretId) {
        invMap.remove(secretId);

        WinningsMeta meta = metaMap.remove(secretId);
        if (meta != null) {
            publicToSecretIdMap.remove(meta.publicId());
        }
    }

    public String getSecretID(String publicID) {
        return publicToSecretIdMap.getOrDefault(publicID, null);
    }

    private String issueID() {
        String id = RandomStringUtils.randomAlphabetic(6);
        while (alreadyUsedIDs.contains(id)) {
            id = RandomStringUtils.randomAlphabetic(6);
        }

        alreadyUsedIDs.add(id);
        return id;
    }
}
