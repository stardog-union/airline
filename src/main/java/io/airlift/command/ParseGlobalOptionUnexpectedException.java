package io.airlift.command;

import io.airlift.command.model.OptionMetadata;

public class ParseGlobalOptionUnexpectedException extends ParseException {

	private final OptionMetadata optionMetadata;

	ParseGlobalOptionUnexpectedException(OptionMetadata optionMetadata) {
		super("Found unexpected global option: %s", new Object[] { optionMetadata.getTitle() });
		this.optionMetadata = optionMetadata;
	}

	public OptionMetadata getOptionMetadata() {
		return this.optionMetadata;
	}
}
