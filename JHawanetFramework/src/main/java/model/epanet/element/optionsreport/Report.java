package model.epanet.element.optionsreport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class contains the value of [REPORT] section of a inp.
 *
 */
public final class Report {
	//code contains the code of all report section
	@NotNull private String code;
	
	public Report() {
		this.code = "";
	}

	/**
	 * Copy constructor.
	 * @param report the object to copy
	 * @throws NullPointerException if report is null
	 */
	public Report(Report report) {
		Objects.requireNonNull(report);
		this.code = report.code;
	}
	
	/**
	 * @return the reportCode
	 */
	public @NotNull String getCode() {
		return code;
	}

	/**
	 * Set the report code
	 * @param reportCode the reportCode to set can be a empty string
	 * @throws NullPointerException if reportCode is null
	 */
	public void setCode(@NotNull String reportCode) {
		Objects.requireNonNull(reportCode);
		this.code = reportCode;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("code", code);
		return gson.toJson(map);
	}

	/**
	 * Copy this object.
	 * @return the copy
	 */
	public Report copy() {
		return new Report(this);
	}
}
