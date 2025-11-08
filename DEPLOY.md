# Deploying to Render (Docker) with GitHub

This file contains step-by-step instructions to deploy this Spring Boot application to Render using Docker and a GitHub repository.

Overview
- We build a Docker image using the included `Dockerfile` (multi-stage Maven build + runtime image).
- Push the repo to GitHub and connect the repo to Render.
- (Recommended) Create a managed Postgres instance on Render and set the environment variables so Spring Boot uses it instead of the file-based H2 DB.

Important note about H2 persistence
- The project currently uses a file-based H2 DB at `./data/cricketDB` (see `src/main/resources/application.properties`).
- Render web services have ephemeral filesystems; data in `./data` will not persist across deploys or instance restarts.
- Recommendation: use a managed Postgres instance on Render and set the following env vars in Render: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.

Quick steps

1) Create a GitHub repo (if you haven't already)

   # In PowerShell (adjust email/name and remote URL)
   git init
   git add .
   git commit -m "initial: add Docker + Render deploy files"
   git branch -M main
   git remote add origin https://github.com/<your-username>/<your-repo>.git
   git push -u origin main

2) On Render
- Sign in and click "New" -> "Web Service" -> "Connect a repository" -> choose the GitHub repo.
- For Environment choose "Docker". Render will use the `Dockerfile` at repo root.
- Branch: `main` (or your chosen branch)
- Set Auto-deploy to `On` for continuous deploys on pushes.

3) (Recommended) Create a managed Postgres DB on Render
- In Render: New -> PostgreSQL -> create a database. After creation, note the connection details.
- Set environment variables on the Web Service (in Render service dashboard -> Environment):
  - SPRING_DATASOURCE_URL = jdbc:postgresql://<host>:<port>/<db>
  - SPRING_DATASOURCE_USERNAME = <username>
  - SPRING_DATASOURCE_PASSWORD = <password>
  - (Optional) SPRING_JPA_HIBERNATE_DDL_AUTO = update

4) Deploy & Logs
- Once connected, Render will build the Docker image (it runs `docker build` using your Dockerfile).
- Use Render's deploy logs to watch the Maven build. The Dockerfile runs the Maven build stage and then packages a jar.
- After deployment, the service should be available at the assigned Render URL. Check logs for Spring Boot startup and DB connection.

Tips & troubleshooting
- If Maven build fails due to memory, increase the build memory or add MAVEN_OPTS in Render build environment.
- If you want to keep H2 for quick testing, you can set no DB env vars and the app will still use file-based H2 — but remember data is ephemeral on Render.
- To use the H2 console in production, enable it only for debugging; it's not recommended for public-facing services.

Environment variables that Spring recognizes (examples)
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SPRING_JPA_HIBERNATE_DDL_AUTO

If you want, I can:
- Create a small script to build the Docker image locally and run it for testing.
- Draft GitHub Actions workflow to build the image and push to GitHub Container Registry (GHCR) as an alternative to building on Render.
