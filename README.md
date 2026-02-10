# Branch Take Home Assignment - GitHub User Proxy Service

This Spring Boot Application exposes a single rest endpoint that takes a GitHub user's username as a path param.
It retrieves the user's profile information and a list of their public
repositories from GitHub, transforms the upstream responses, and returns a simplified
payload pursuant to the spec given in this assignment's instructions.

## Assumptions
* Java 17 is available locally
* Gradle is used as the tool, via gradle wrapper
* Network access to `api.github.com` is available

## Authentication
Authentication is optional. This application supports including a bearer
token via the presence of a `GITHUB_PAT` environment variable. If none
is found, an unauthenticated request will be sent. The GitHub APIs being
called are public, however unauthenticated requests are subject to stricter
rate limits.

## Technology Stack
I have chosen standard, well-documented, and battle tested libraries for this assignment, following the KISS principle.
* Java 17
* Spring Boot 3.3.8
* Spring Boot WebMVC
* Jackson
* JUnit
* Mockito

## Configuration

### Optional GitHub Personal Access Token
To avoid rate limiting, you may supply a [GitHub Personal Access Token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens).
Set the `GITHUB_PAT` environment variable, e.g. `export GITHUB_PAT={{YOUR_TOKEN_HERE}}`.
If it is not provided, the application will make unauthenticated requests to the upstream GitHub endpoints.

## Running Locally
1. Clone the repository
```shell
git clone <repo-url>
cd <repo>
```
2. Build the application
```
./gradlew clean build
```
3. Start the service
```
./gradlew bootRun
```
The application will start at `http://localhost:8080`

## API Usage
### Endpoint
```
GET /github.com/users/{username}
```
### Example
```
curl http://localhost:8080/users/octocat
```

### Example Response
```
{
  "user_name" : "octocat",
  "display_name" : "The Octocat",
  "avatar" : "https://avatars.githubusercontent.com/u/583231?v=4",
  "geo_location" : "San Francisco",
  "email" : null,
  "url" : "https://api.github.com/users/octocat",
  "created_at" : "Tue, 25 Jan 2011 18:44:36 GMT",
  "repos" : [ {
    "name" : "boysenberry-repo-1",
    "url" : "https://api.github.com/repos/octocat/boysenberry-repo-1"
  }, {
    "name" : "git-consortium",
    "url" : "https://api.github.com/repos/octocat/git-consortium"
  }, {
    "name" : "hello-worId",
    "url" : "https://api.github.com/repos/octocat/hello-worId"
  }, {
    "name" : "Hello-World",
    "url" : "https://api.github.com/repos/octocat/Hello-World"
  }, {
    "name" : "linguist",
    "url" : "https://api.github.com/repos/octocat/linguist"
  }, {
    "name" : "octocat.github.io",
    "url" : "https://api.github.com/repos/octocat/octocat.github.io"
  }, {
    "name" : "Spoon-Knife",
    "url" : "https://api.github.com/repos/octocat/Spoon-Knife"
  }, {
    "name" : "test-repo1",
    "url" : "https://api.github.com/repos/octocat/test-repo1"
  } ]
}
```
> Note: The compact array formatting is a deliberate choice given the structure on the assignment instructions; the data is valid either way

## Error Handling
Error responses returned by GitHub are mapped to appropriate statuses

|GitHub Response| Service Response|
|---------------|-----------------|
| 404 | 404 Not Found |
| 401 | 401 Unauthorized |
| 403 | 403 Forbidden |
| 429 | 429 Too Many requests|
| 5xx | 502 Bad Gateway | 

Errors are returned as structured JSON with a human friendly message and details where available

## Testing
The full test suite can be run as:
```
./gradlew test
```

Test coverage includes:
* Controller slice tests
* Service unit tests
* Authorization header behavior
* Error Mapping

## Design Notes
* Spring's `RestClient` is used for outbound HTTP calls
* Configuration is externalized via `@ConfigurationProperties`
* Authentication header is conditionally applied when a PAT is present as an environment variable
* Jackson is configured globally for snake_case output and ISO timestamps
* All outbound dependencies are mocked in unit tests

## Project Structure
| package | contents                           |
| --------|------------------------------------|
| config/ | Configuration beans and properties |
|controller/| REST Controller(s)                 |
|service/| Business logic                     |
|exceptions/| Exception model classes            |
|model/| DTOs                               |
|web/| Exception handling|                 

## Potential future improvements
* Add OpenAPI or Swagger documentation
* Add retries/backoff for transient upstream issues and gremlins
* Add caching for repeated lookups