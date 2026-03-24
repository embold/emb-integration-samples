# emb-integration-samples

Sample source code and GitHub Actions workflows for integrating [Embold](https://embold.io/) static code analysis across multiple languages.

## Repository Structure

```
sample_source_code/
  java/          — Java 17 (Maven)
  cpp/           — C++17 (CMake)
  csharp/        — C# / .NET 8
  python/        — Python 3
  javascript/    — Node.js (CommonJS)
  typescript/    — TypeScript 5 (compiled to ESM)

.github/workflows/
  embold-java.yml
  embold-cpp.yml
  embold-csharp.yml
  embold-python.yml
  embold-javascript.yml
  embold-typescript.yml
```

Each language folder contains **10 interconnected source files** demonstrating:
- Interface definition (`IAnimal`, `IVisitor`)
- Abstract base class (`Animal`)
- Concrete subclasses with inheritance (`Dog`, `Cat`)
- Composition (`Shelter`)
- Visitor pattern (`AnimalVisitor`)
- Utility methods: recursion, memoisation, statistics (`MathUtils`)
- Orchestration (`Report`)
- Entry point (`Main` / `main`)

## Embold GitHub Action Integration

Every workflow uses [`embold/github-action-docker@v2.0.0`](https://github.com/marketplace/actions/embold-github-actions):

| Workflow | Language | Path filter |
|----------|----------|-------------|
| `embold-java.yml`       | Java        | `sample_source_code/java/**` |
| `embold-cpp.yml`        | C/C++       | `sample_source_code/cpp/**` |
| `embold-csharp.yml`     | C#          | `sample_source_code/csharp/**` |
| `embold-python.yml`     | Python      | `sample_source_code/python/**` |
| `embold-javascript.yml` | JavaScript  | `sample_source_code/javascript/**` |
| `embold-typescript.yml` | TypeScript  | `sample_source_code/typescript/**` |

Workflows trigger on push to `master` (filtered to their language folder) and via `workflow_dispatch`.

## Setup

### 1. Add the Embold token secret

In your GitHub repository go to **Settings → Secrets and variables → Actions** and add:

| Secret name | Value |
|-------------|-------|
| `EMBOLD_TOKEN_DEMO_INST` | Your Embold API token for `https://demo.embold.io/` |

### 2. Replace `emboldRepoUid` placeholders

Each `repository-configuration.json` and workflow file contains a placeholder for the repository UID that must be set to the actual UID registered on the Embold demo instance:

| File | Placeholder | Replace with |
|------|-------------|--------------|
| `sample_source_code/java/repository-configuration.json` | `<REPO_UID_JAVA>` | UID from Embold UI |
| `sample_source_code/cpp/repository-configuration.json`  | `<REPO_UID_CPP>`  | UID from Embold UI |
| `sample_source_code/csharp/repository-configuration.json` | `<REPO_UID_CSHARP>` | UID from Embold UI |
| `sample_source_code/python/repository-configuration.json` | `<REPO_UID_PYTHON>` | UID from Embold UI |
| `sample_source_code/javascript/repository-configuration.json` | `<REPO_UID_JS>` | UID from Embold UI |
| `sample_source_code/typescript/repository-configuration.json` | `<REPO_UID_TS>` | UID from Embold UI |

Also update the matching `emboldRepoUid:` value in each `.github/workflows/embold-<lang>.yml`.

### 3. Trigger a scan

Push a change to any language folder, or manually dispatch a workflow from the **Actions** tab.

## Quality Gate

Each workflow fails with exit code 1 if the Embold quality gate returns `FAILED`:

```yaml
- name: Check Quality Gate Result
  if: steps.embold-scan.outputs.qualityGateStatus == 'FAILED'
  run: |
    echo "Quality gate failed!"
    exit 1
```

The `snapshotLabel` is set to `${{ github.run_number }}` so every run gets a unique, auto-incrementing label.

