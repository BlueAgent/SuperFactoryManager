package ca.teamdman.sfm.config;

import net.minecraftforge.common.ForgeConfigSpec;

final class ClientConfig {
	final ForgeConfigSpec.IntValue cliInt;

	ClientConfig(final ForgeConfigSpec.Builder builder) {
		builder.push("General Category");
		cliInt = builder
				.comment("Client Int")
				.defineInRange("cliInt", 0, 0, 1);
		builder.pop();
	}
}
