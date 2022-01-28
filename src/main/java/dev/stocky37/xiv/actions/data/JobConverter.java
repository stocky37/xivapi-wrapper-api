package dev.stocky37.xiv.actions.data;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.function.Function;
import javax.inject.Singleton;

@Singleton
public class JobConverter implements Function<JsonNode, Job> {

	public static final String ID = "ID";
	public static final String NAME = "Name";
	public static final String ABBREV = "Abbreviation";
	public static final String ICON = "Icon";
	public static final String CATEGORY = "ClassJobCategoryTargetID";
	public static final String ROLE = "Role";
	public static final String JOB_INDEX = "JobIndex";
	public static final String IS_LIMITED = "IsLimitedJob";

	public static final List<String> ALL_FIELDS = List.of(
		ID, NAME, ABBREV, ICON, CATEGORY, ROLE, JOB_INDEX, IS_LIMITED
	);

	@Override
	public Job apply(JsonNode json) {
		return new Job(
			json.get(ID).asText(),
			json.get(NAME).asText(),
			json.get(ABBREV).asText(),
			json.get(ICON).asText(),
			category(json.get(CATEGORY).asInt()),
			type(json.get(JOB_INDEX).asInt()),
			role(json.get(ROLE).asInt()),
			json.get(JOB_INDEX).asInt(),
			json.get(IS_LIMITED).asBoolean()
		);
	}

	private Job.Category category(int categoryId) {
		return switch(categoryId) {
			case 30 -> Job.Category.DOW;
			case 31 -> Job.Category.DOM;
			case 32 -> Job.Category.DOL;
			case 33 -> Job.Category.DOH;
			default -> throw new RuntimeException("Unknown category: " + categoryId);
		};
	}

	private Job.Type type(int jobIndex) {
		return jobIndex > 0 ? Job.Type.JOB : Job.Type.CLASS;
	}

	private Job.Role role(int role) {
		return switch(role) {
			case 0 -> Job.Role.NON_BATTLE;
			case 1 -> Job.Role.TANK;
			case 2 -> Job.Role.MELEE_DPS;
			case 3 -> Job.Role.RANGED_DPS;
			case 4 -> Job.Role.HEALER;
			default -> throw new RuntimeException("Unknown role: " + role);
		};
	}
}
