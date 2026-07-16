package com.projects.job_tracker.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class JobEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@ManyToOne(optional = false)
	@JoinColumn(name = "company_id", nullable = false)
	private CompanyEntity company;

	@Column(columnDefinition = "TEXT")
	private String description;

	private String location;

	@Column(name = "salary_min")
	private BigDecimal salaryMin;

	@Column(name = "salary_max")
	private BigDecimal salaryMax;

	@Column(nullable = false)
	private String source;

	@Column(nullable = false)
	private String url;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "external_id")
	private String externalId;

	@Column(name = "posted_at")
	private Instant postedAt;

	@Column(name = "employment_type")
	private String employmentType;

	@Column(name = "work_mode")
	private String workMode;

	private String category;

	private String subcategory;

	@Column(columnDefinition = "TEXT")
	private String benefits;

	@Column(columnDefinition = "TEXT")
	private String requirements;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public CompanyEntity getCompany() {
		return company;
	}

	public void setCompany(CompanyEntity company) {
		this.company = company;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public BigDecimal getSalaryMin() {
		return salaryMin;
	}

	public void setSalaryMin(BigDecimal salaryMin) {
		this.salaryMin = salaryMin;
	}

	public BigDecimal getSalaryMax() {
		return salaryMax;
	}

	public void setSalaryMax(BigDecimal salaryMax) {
		this.salaryMax = salaryMax;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Instant getPostedAt() {
		return postedAt;
	}

	public void setPostedAt(Instant postedAt) {
		this.postedAt = postedAt;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public String getWorkMode() {
		return workMode;
	}

	public void setWorkMode(String workMode) {
		this.workMode = workMode;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	public String getBenefits() {
		return benefits;
	}

	public void setBenefits(String benefits) {
		this.benefits = benefits;
	}

	public String getRequirements() {
		return requirements;
	}

	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}
}
