# # Database Design

SQLite is used as the primary database.

## Tables

### jobs
- id
- title
- company_id
- description
- location
- salary_min
- salary_max
- source
- url
- created_at

### applications
- id
- job_id
- status
- applied_at
- notes

### companies
- id
- name
- website

### search_profiles
- id
- name
- keywords
- filters

## Notes

- Flyway is used for migrations
- JPA entities are not exposed outside infrastructure layer