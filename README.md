# AI Resume Analyzer

A full-stack web app that analyzes a PDF resume with a large language model and returns strengths, weaknesses, improvement suggestions, and an overall score.

**Live demo:** https://ai-resume-analyzer-1-mnha.onrender.com

> **Note:** The backend runs on Render's free tier and sleeps after about 15 minutes of inactivity. The first request after idle can take 30-60 seconds while it wakes up. After that, it responds normally.

## Features

- Upload a PDF resume from the browser
- Server-side text extraction with Apache PDFBox
- AI analysis covering strengths, weaknesses, actionable suggestions, and a score out of 100
- Input validation (PDF only, 10MB limit)
- Fully containerized backend, deployed to the cloud

## Tech Stack

| Layer | Technology |
| --- | --- |
| Frontend | React, Vite, CSS |
| Backend | Java 17, Spring Boot, REST API |
| PDF parsing | Apache PDFBox |
| AI | Groq API (Llama 3.3 70B) via OkHttp |
| Deployment | Docker, Render (Web Service + Static Site) |

## Architecture

```
React (Static Site)  --POST /api/resume/analyze-->  Spring Boot (Docker)
                         multipart PDF                 |
                                                       |-- PDFBox: extract text
                                                       |-- Prompt + resume text --> Groq API
                                                       v
                     <------ analysis text ------  response
```

## API

### `POST /api/resume/analyze`

Accepts a PDF as `multipart/form-data` and returns the analysis as plain text.

| Field | Type | Description |
| --- | --- | --- |
| `file` | file | The resume, `application/pdf` only |

**Responses**

- `200 OK` with the analysis text
- `400 Bad Request` if the file is not a PDF

```bash
curl -X POST https://ai-resume-analyzer-1stu.onrender.com/api/resume/analyze \
  -F "file=@resume.pdf;type=application/pdf"
```

## Running Locally

### Prerequisites

- Java 17
- Node.js (LTS)
- A free [Groq API key](https://console.groq.com)

### Backend

```bash
# Set your key as an environment variable (PowerShell)
$env:GROQ_API_KEY="your_key_here"

# Run the app
./gradlew bootRun
```

The API starts on `http://localhost:8080`. If you run it from IntelliJ, add `GROQ_API_KEY` under the run configuration's environment variables instead.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The app opens on `http://localhost:5173`. By default it talks to `http://localhost:8080`. To point it somewhere else, set `VITE_API_URL` before building:

```bash
VITE_API_URL=https://your-backend.onrender.com npm run build
```

### Docker

```bash
docker build -t ai-resume-analyzer .
docker run -p 8080:8080 -e GROQ_API_KEY=your_key_here ai-resume-analyzer
```

## Deployment

- **Backend:** Render Web Service built from the `Dockerfile` (multi-stage build). `GROQ_API_KEY` is set as an environment variable in Render.
- **Frontend:** Render Static Site with root directory `frontend`, build command `npm install && npm run build`, publish directory `dist`, and `VITE_API_URL` set to the backend URL.
- **CORS:** The backend allows requests from `localhost:5173` and the deployed frontend origin.

## What I Learned

- Building a REST API with Spring Boot and handling multipart file uploads
- Extracting text from PDFs and prompt engineering for structured AI feedback
- Calling an external LLM API over HTTP and keeping secrets out of source control
- Debugging CORS between separately hosted frontend and backend services
- Containerizing a Spring Boot app with a multi-stage Dockerfile and deploying it on Render
- State, hooks, and conditional rendering in React

## Roadmap

- [ ] Score a resume against a pasted job description
- [ ] Render the AI output as formatted markdown
- [ ] Save analysis history (external free Postgres such as Neon or Supabase)
- [ ] Response caching to avoid repeat API calls
