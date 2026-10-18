import { useState } from 'react'
import './App.css'

function App() {
  const [file, setFile] = useState(null)
    const [resumeId, setResumeId] = useState(null)
    const [analysis, setAnalysis] = useState('')
  const [loading, setLoading] = useState(false)

    async function handleUpload(selectedFile) {
    if (!selectedFile) return
    const formData = new FormData()
    formData.append('file', selectedFile)

    const uploadResponse = await fetch('http://localhost:8080/api/resume', {
      method: 'POST',
      body: formData,
    })

    const resume = await uploadResponse.json()

    setResumeId(resume.id);
  }

  async function handleAnalyze(){
      setLoading(true)

      const analyzeResponse = await fetch(`http://localhost:8080/api/resume/${resumeId}/analyze`, {
          method: 'POST',
      })

      const result = await analyzeResponse.text();
      setAnalysis(result)
      console.log(result)
      setLoading(false)
  }

  return (
      <div className="container">
          <div className="card">
        <h1 className="title">AI Resume Analyzer</h1>
        <p className="subtitle">Upload your resume and get instant AI feedback</p>

       <label className="upload-area">
        <input type="file" accept=".pdf" onChange={(e) => {
            const selectedFile = e.target.files[0]
            setFile(selectedFile)
            handleUpload(selectedFile)
        }}/>
        <span>{file ? file.name : 'Click to upload your PDF resume'}</span>
        </label>

         <button className="button" onClick={handleAnalyze} disabled={!resumeId || loading}>
             {loading ? 'Analyzing...' : 'Analyze Resume'}
         </button>

              {analysis && (
                  <div className="analysis">
                      <h2>Analysis Results</h2>
                      <pre>{analysis + "test"}</pre>
                  </div>
                  )}
      </div>
      </div>
  )
}

export default App
