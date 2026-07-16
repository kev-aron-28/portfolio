package com.projects.job_tracker.presentation.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createsAndListsJobs() throws Exception {
		String jobPayload = """
				{
				  "title": "Java Developer",
				  "companyName": "TechCorp",
				  "companyWebsite": "https://techcorp.com",
				  "description": "Spring Boot role",
				  "location": "Mexico City",
				  "salaryMin": 50000,
				  "salaryMax": 80000,
				  "source": "linkedin",
				  "url": "https://example.com/jobs/java-dev"
				}
				""";

		mockMvc.perform(post("/jobs").contentType(MediaType.APPLICATION_JSON).content(jobPayload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.title").value("Java Developer"));

		mockMvc.perform(get("/jobs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value("Java Developer"));
	}

	@Test
	void createsApplicationAndUpdatesStatus() throws Exception {
		String jobPayload = """
				{
				  "title": "QA Engineer",
				  "companyName": "TestInc",
				  "source": "occ",
				  "url": "https://example.com/jobs/qa"
				}
				""";

		MvcResult jobResult = mockMvc.perform(post("/jobs").contentType(MediaType.APPLICATION_JSON).content(jobPayload))
				.andExpect(status().isCreated())
				.andReturn();

		String jobBody = jobResult.getResponse().getContentAsString();
		long jobId = ((Number) com.jayway.jsonpath.JsonPath.read(jobBody, "$.id")).longValue();

		String applicationPayload = """
				{
				  "jobId": %d,
				  "status": "APPLIED",
				  "notes": "Applied via company site"
				}
				""".formatted(jobId);

		MvcResult applicationResult = mockMvc.perform(
						post("/applications").contentType(MediaType.APPLICATION_JSON).content(applicationPayload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value("APPLIED"))
				.andReturn();

		String applicationBody = applicationResult.getResponse().getContentAsString();
		long applicationId = ((Number) com.jayway.jsonpath.JsonPath.read(applicationBody, "$.id")).longValue();

		String updatePayload = """
				{
				  "status": "INTERVIEWING",
				  "notes": "Phone screen scheduled"
				}
				""";

		mockMvc.perform(patch("/applications/" + applicationId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(updatePayload))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("INTERVIEWING"));
	}

	@Test
	void createsAndListsSearchProfiles() throws Exception {
		String profilePayload = """
				{
				  "name": "Backend Remote",
				  "keywords": "java,spring,remote",
				  "filters": "{\\"remote\\": true}"
				}
				""";

		mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(profilePayload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Backend Remote"));

		mockMvc.perform(get("/profiles"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].keywords").value("java,spring,remote"));
	}
}
