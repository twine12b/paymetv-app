export default function Home() {
  return (
    <div className="min-h-screen relative overflow-hidden">
      {/* Background Video */}
      <video
        className="fixed top-1/2 left-1/2 min-w-full min-h-full w-auto h-auto -z-10 transform -translate-x-1/2 -translate-y-1/2 object-cover"
        autoPlay
        loop
        muted
        playsInline
        poster="https://mocha-cdn.com/01997ba3-0627-7f61-a567-887abc29a7b4/hero-background.jpg"
      >
        <source src="https://paymetv.co.uk/Video/System/backvideo2.mp4" type="video/mp4" />
        {/* Fallback to background image if video fails to load */}
        <div
          className="fixed top-1/2 left-1/2 min-w-full min-h-full w-auto h-auto -z-10 transform -translate-x-1/2 -translate-y-1/2 bg-cover bg-center"
          style={{
            backgroundImage: `url('https://mocha-cdn.com/01997ba3-0627-7f61-a567-887abc29a7b4/hero-background.jpg')`
          }}
        />
      </video>

      {/* Main container */}
      <div className="container mx-auto px-4 sm:px-6 lg:px-8 h-screen flex flex-col text-center text-white max-w-7xl relative">

        {/* Logo and Slogan - Top Section */}
        <div className="flex flex-col items-center pt-16 sm:pt-20 md:pt-24 lg:pt-28">
          {/* Logo */}
          <div className="mb-4 sm:mb-6 md:mb-8">
            <img
              src="https://paymetv.co.uk/images/system/tvlogo-whiteout2.svg"
              alt="PayMe TV"
              className="w-[280px] sm:w-[320px] md:w-[400px] lg:w-[480px] xl:w-[520px] mx-auto max-w-[90vw] h-auto"
            />
            <h2 className="text-sm sm:text-base md:text-lg lg:text-xl xl:text-2xl italic font-light text-white/90 leading-relaxed px-4 max-w-2xl mx-auto">
              The fast track way to get paid for your videos
            </h2>
          </div>
        </div>

        {/* Coming Soon Message - Center of Screen */}
        <div className="flex-1 flex items-center justify-center">
          <div>
            <h1 className="text-2xl sm:text-3xl md:text-4xl lg:text-5xl xl:text-6xl font-bold italic text-white drop-shadow-2xl px-4 leading-tight">
              COMING SOON!!!..
            </h1>
          </div>
        </div>
      </div>

      {/* Footer */}
      <div className="fixed bottom-0 left-0 right-0 text-center text-white/70 text-[10px] sm:text-xs md:text-sm p-3 sm:p-4 md:p-6 bg-black/30 backdrop-blur-sm">
        <div className="max-w-4xl mx-auto px-2">
          <p className="leading-relaxed">
            Copyright © 2011 PayMeTV enterprises.<br className="sm:hidden md:inline lg:hidden" />
            <span className="hidden sm:inline md:hidden lg:inline"> </span>
            <span className="block sm:inline">All rights reserved. Use of this website signifies your agreement to the Terms of Use and Online Privacy Policy (updated 01-07-2025).</span>
          </p>
        </div>
      </div>
    </div>
  );
}