import { useState, useRef, ChangeEvent, FormEvent } from 'react';

type UploadStatus = 'idle' | 'uploading' | 'success' | 'error';

interface UploadResult {
  originalName: string;
  storedName: string;
  size: number;
  contentType: string;
  status: string;
  message?: string;
}

const ALLOWED_TYPES = [
  'image/jpeg', 'image/png', 'image/gif', 'image/webp',
  'video/mp4', 'video/mpeg', 'application/pdf',
];
const MAX_SIZE_MB = 10;

const FileUploadPage = () => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [uploadStatus, setUploadStatus] = useState<UploadStatus>('idle');
  const [result, setResult] = useState<UploadResult | null>(null);
  const [clientError, setClientError] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] ?? null;
    setSelectedFile(file);
    setClientError(null);
    setResult(null);
    setUploadStatus('idle');
  };

  const validate = (file: File): string | null => {
    if (!ALLOWED_TYPES.includes(file.type)) {
      return `File type "${file.type}" is not allowed. Accepted: JPEG, PNG, GIF, WebP, MP4, MPEG, PDF.`;
    }
    if (file.size > MAX_SIZE_MB * 1024 * 1024) {
      return `File is too large (${(file.size / 1024 / 1024).toFixed(1)} MB). Maximum size is ${MAX_SIZE_MB} MB.`;
    }
    return null;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!selectedFile) return;

    const validationError = validate(selectedFile);
    if (validationError) {
      setClientError(validationError);
      return;
    }

    setUploadStatus('uploading');
    setClientError(null);

    const formData = new FormData();
    formData.append('file', selectedFile);

    try {
      const response = await fetch('/api/files/upload', {
        method: 'POST',
        body: formData,
      });

      const data: UploadResult = await response.json();

      if (response.ok) {
        setUploadStatus('success');
        setResult(data);
      } else {
        setUploadStatus('error');
        setResult(data);
      }
    } catch {
      setUploadStatus('error');
      setResult({ originalName: '', storedName: '', size: 0, contentType: '', status: 'error', message: 'Network error – could not reach the server.' });
    }
  };

  const handleReset = () => {
    setSelectedFile(null);
    setUploadStatus('idle');
    setResult(null);
    setClientError(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  return (
    <div className="min-h-screen bg-gray-900 flex items-center justify-center p-6">
      <div className="w-full max-w-lg bg-gray-800 rounded-2xl shadow-xl p-8">
        <h1 className="text-2xl font-bold text-white mb-2">File Upload</h1>
        <p className="text-gray-400 text-sm mb-6">
          Accepted: JPEG, PNG, GIF, WebP, MP4, MPEG, PDF &mdash; max {MAX_SIZE_MB}&nbsp;MB
        </p>

        <form onSubmit={handleSubmit} className="space-y-5">
          {/* Drop zone / file input */}
          <label className="block cursor-pointer">
            <div className={`flex flex-col items-center justify-center border-2 border-dashed rounded-xl p-8 transition-colors
              ${selectedFile ? 'border-indigo-500 bg-indigo-900/20' : 'border-gray-600 hover:border-indigo-400 bg-gray-700/30'}`}>
              <svg className="w-10 h-10 text-gray-400 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
                  d="M12 16v-8m0 0-3 3m3-3 3 3M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1" />
              </svg>
              {selectedFile ? (
                <span className="text-indigo-300 font-medium text-sm text-center break-all">{selectedFile.name}</span>
              ) : (
                <span className="text-gray-400 text-sm">Click to select a file</span>
              )}
              {selectedFile && (
                <span className="text-gray-500 text-xs mt-1">
                  {(selectedFile.size / 1024).toFixed(1)}&nbsp;KB &bull; {selectedFile.type}
                </span>
              )}
            </div>
            <input ref={fileInputRef} type="file" className="hidden" onChange={handleFileChange}
              accept={ALLOWED_TYPES.join(',')} />
          </label>

          {/* Client-side validation error */}
          {clientError && (
            <p className="text-red-400 text-sm bg-red-900/20 border border-red-700 rounded-lg px-4 py-2">{clientError}</p>
          )}

          {/* Action buttons */}
          <div className="flex gap-3">
            <button type="submit" disabled={!selectedFile || uploadStatus === 'uploading'}
              className="flex-1 bg-indigo-600 hover:bg-indigo-500 disabled:bg-indigo-900 disabled:text-indigo-500 text-white font-semibold py-2.5 rounded-xl transition-colors">
              {uploadStatus === 'uploading' ? 'Uploading…' : 'Upload'}
            </button>
            <button type="button" onClick={handleReset}
              className="px-5 bg-gray-700 hover:bg-gray-600 text-gray-300 font-semibold py-2.5 rounded-xl transition-colors">
              Reset
            </button>
          </div>
        </form>

        {/* Result banner */}
        {result && (
          <div className={`mt-6 rounded-xl p-4 text-sm ${uploadStatus === 'success' ? 'bg-green-900/30 border border-green-700 text-green-300' : 'bg-red-900/30 border border-red-700 text-red-300'}`}>
            {uploadStatus === 'success' ? (
              <>
                <p className="font-semibold mb-1">✓ Upload successful</p>
                <p>Original name: <span className="font-mono">{result.originalName}</span></p>
                <p>Stored as: <span className="font-mono">{result.storedName}</span></p>
                <p>Size: {(result.size / 1024).toFixed(1)}&nbsp;KB &bull; {result.contentType}</p>
              </>
            ) : (
              <>
                <p className="font-semibold mb-1">✕ Upload failed</p>
                <p>{result.message}</p>
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default FileUploadPage;

