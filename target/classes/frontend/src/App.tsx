import './App.css'
import Footer from './components/Footer'
import Logo from './components/Logo'
import Message from './components/Message'
import Slogan from './components/Slogan'
import VideoBackground from './components/VideoBackground'
import FileUploadPage from './components/FileUploadPage'
import './output.css'

// Simple pathname-based routing — no external router needed.
// The Spring Boot SpaForwardController forwards /upload to index.html
// so React can render the correct component.
const isUploadPage =
  window.location.pathname === '/upload' ||
  window.location.pathname === '/upload/';

function LandingPage() {
  return (
    <>
      <VideoBackground />
      <div className="relative w-full h-full text-white">
        {/* Logo and Slogan positioned near the top */}
        <div className="top-5 sm:top-12 md:top-16 lg:top-20 w-full px-4 sm:px-6 md:px-8 lg:px-12 flex flex-col items-center">
          <Logo />
          <div className="relative flex flex-col top-5 sm:top-2">
            <Slogan />
          </div>
        </div>

        {/* Message centered in viewport */}
        <div className="relative top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full px-4 top-75 sm:px-6 md:top-15 md:px-8 md:top-15 lg:px-12 flex flex-col sm:blue-500">
          <Message />
        </div>

        {/* Footer at bottom */}
        <Footer />
      </div>
    </>
  );
}

function App() {
  if (isUploadPage) {
    return (
      <>
        {/* Back link so users can return to the landing page */}
        <a
          href="/"
          className="fixed top-4 left-4 z-50 inline-flex items-center gap-1 bg-gray-800 hover:bg-gray-700
                     text-white text-sm font-semibold px-4 py-2 rounded-xl shadow transition-colors"
        >
          ← Back
        </a>
        <FileUploadPage />
      </>
    );
  }
  return <LandingPage />;
}

export default App
