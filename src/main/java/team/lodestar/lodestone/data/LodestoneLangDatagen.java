package team.lodestar.lodestone.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import team.lodestar.lodestone.helpers.DataHelper;
import team.lodestar.lodestone.registry.common.LodestoneAttributes;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static team.lodestar.lodestone.LodestoneLib.LODESTONE;

public class LodestoneLangDatagen extends FabricLanguageProvider {

    protected LodestoneLangDatagen(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {

        translationBuilder.add("attribute.name.lodestone.magic_resistance", "Magic Resistance");
        translationBuilder.add("attribute.name.lodestone.magic_proficiency", "Magic Proficiency");
        translationBuilder.add("attribute.name.lodestone.magic_damage", "Magic Damage");

        addOption(translationBuilder, "screenshake_intensity", "Screenshake Intensity");
        addOptionTooltip(translationBuilder, "screenshake_intensity", "Controls how much screenshake is applied to your screen.");

        addOption(translationBuilder, "fire_offset", "Fire Overlay Offset");
        addOptionTooltip(translationBuilder, "fire_offset", "Offsets the fire overlay effect downwards, clearing up your vision.");

        addCommand(translationBuilder, "devsetup", "World setup for not-annoying development work");
        addCommand(translationBuilder, "screenshake", "Command Successful, enjoy your screenshake.");

    }

    public void addCommand(TranslationBuilder translationBuilder, String command, String feedback) {
        translationBuilder.add(getCommand(command), feedback);
    }

    public static String getCommand(String command) {
        return "command." + LODESTONE + "." + command;
    }

    public void addOption(TranslationBuilder translationBuilder, String option, String result) {
        translationBuilder.add(getOption(option), result);
    }

    public static String getOption(String option) {
        return "options." + LODESTONE + "." + option;
    }

    public void addOptionTooltip(TranslationBuilder translationBuilder, String option, String result) {
        translationBuilder.add(getOptionTooltip(option), result);
    }

    public static String getOptionTooltip(String option) {
        return "options." + LODESTONE + "." + option + ".tooltip";
    }

    @Override
    public String getName() {
        return "Lodestone Lang Entries";
    }
}