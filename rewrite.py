import subprocess

env_filter = """
if [ "$GIT_AUTHOR_NAME" = "dependabot[bot]" ]; then
    export GIT_AUTHOR_NAME="sandeep mehta"
    export GIT_AUTHOR_EMAIL="sandeepmehta.tech@gmail.com"
fi
if [ "$GIT_COMMITTER_NAME" = "dependabot[bot]" ]; then
    export GIT_COMMITTER_NAME="sandeep mehta"
    export GIT_COMMITTER_EMAIL="sandeepmehta.tech@gmail.com"
fi
"""

print("Running filter-branch...")
result = subprocess.run(
    ["git", "filter-branch", "--env-filter", env_filter, "--force", "--", "--all"],
    capture_output=True,
    text=True
)

print(result.stdout)
print(result.stderr)

