package com.clusterat.live.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingDTO {
    private Long id;
    @JsonProperty("job_posting_id")
    private String jobPostingId;
    @JsonProperty("job_title")
    private String jobTitle;
    @JsonProperty("source_url")
    private String sourceUrl;
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("company_url")
    private String companyUrl;
    @JsonProperty("external_company_id")
    private String externalCompanyId;
    @JsonProperty("job_location")
    private String jobLocation;
    @JsonProperty("employment_type")
    private String employmentType;
    @JsonProperty("seniority_level")
    private String seniorityLevel;
    @JsonProperty("job_function")
    private String jobFunction;
    @JsonProperty("job_industries")
    private String jobIndustries;
    @JsonProperty("job_summary_text")
    private String jobSummaryText;
    @JsonProperty("salary_currency")
    private String salaryCurrency;
    @JsonProperty("salary_min")
    private BigDecimal salaryMin;
    @JsonProperty("salary_max")
    private BigDecimal salaryMax;
    @JsonProperty("salary_payment_period")
    private String salaryPaymentPeriod;
    @JsonProperty("apply_link")
    private String applyLink;
    @JsonProperty("is_easy_apply")
    private Boolean isEasyApply;
    @JsonProperty("is_application_available")
    private Boolean isApplicationAvailable;
    @JsonProperty("num_applicants")
    private Short numApplicants;
    @JsonProperty("job_poster_name")
    private String jobPosterName;
    @JsonProperty("job_poster_title")
    private String jobPosterTitle;
    @JsonProperty("job_posted_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX][X]")
    private OffsetDateTime jobPostedAt;
    @JsonProperty("scraped_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX][X]")
    private OffsetDateTime scrapedAt;
}

