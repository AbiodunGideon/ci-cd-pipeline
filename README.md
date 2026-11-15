# Java Security Pipeline Demo

A tiny Java app plus a GitHub Actions pipeline that adds OWASP-style security
scanning **without blocking developers**. Findings are alerts to review later;
only a failed build or a leaked secret stops the pipeline.

> **Warning:** this app contains intentionally insecure code and an
> intentionally vulnerable dependency so the scanners have something to find.
> It is for learning and testing only. Do not deploy it publicly or reuse the code.

## What is in here

| Path | Purpose |
|---|---|
| `src/main/java/demo/App.java` | Minimal HTTP server (JDK only): `/health` and `/hello?name=` |
| `src/main/java/demo/InsecureExamples.java` | Deliberately flawed code for SAST to flag |
| `pom.xml` | Maven build; includes log4j-core 2.14.1 (Log4Shell) for SCA to flag |
| `.github/workflows/ci-cd-security.yml` | The pipeline |

## Run it locally

Requires JDK 21 and Maven.

```bash
mvn package
java -jar target/demo-app-1.0.0.jar
# then open http://localhost:8080/hello?name=Sam
```

## Pipeline stages

| Stage | Tool | OWASP area | Gate |
|---|---|---|---|
| Build and test | Maven | - | Hard (must compile and pass) |
| SCA | OWASP Dependency-Check | A06 Vulnerable components | Soft (alerts only) |
| SAST | Semgrep | A02, A03, A05 and more | Soft |
| Secret scan | Gitleaks | Leaked credentials | **Hard** |
| DAST | OWASP ZAP baseline | A01, A05, A07 (passive checks) | Soft |

## One-time repo setup

1. **NVD API key (strongly recommended).** Request a free key at
   https://nvd.nist.gov/developers/request-an-api-key, then add it as a repo
   secret named `NVD_API_KEY` (Settings, Secrets and variables, Actions).
   Without it, the SCA job downloads the vulnerability database very slowly
   and may time out.
2. **Security tab.** Enable code scanning (Settings, Code security). On private
   repos this needs GitHub Advanced Security; if it is unavailable the upload
   steps fail harmlessly and the reports are still downloadable from the
   run's Artifacts section.
3. **Branch protection.** For `main`, require only the checks `Build and test`
   and `Secret scan (Gitleaks)`. Leave SCA, SAST and DAST unrequired, which is
   what makes them soft gates.

## Try it: three tests

**1. Clean run.** Push to `main` (or open a PR). Expect Build and Secret scan
green, and alerts appearing in the Security tab from SCA and SAST. The pipeline
stays green even though findings exist. ZAP's report is under the run's Artifacts.

**2. Hard gate.** On a throwaway branch, add a fake secret, e.g. in `App.java`:

```java
String api_key = "9f8e7d6c5b4a39281706f5e4d3c2b1a0";
```

Open a PR. `Secret scan` should fail and block merging, while the other jobs
still run. Delete the branch afterwards.

**3. Fix and re-scan.** Change the log4j-core version in `pom.xml` to `2.17.2`,
push, and watch the SCA alert for Log4Shell close on the next run.

## License

MIT, see `LICENSE`.
