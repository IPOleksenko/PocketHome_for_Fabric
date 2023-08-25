package dev.ipoleksenko.pockethome;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PocketHomeMod implements ModInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(PocketHomeMod.class);

	@Override
	public void onInitialize() {
		LOGGER.info("O kurwa rakieta!");
	}
}
