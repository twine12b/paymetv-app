import { useState } from 'react'
// import pmtvLogo from './assets/whiteout-tvlogo-Only.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Footer from './components/Footer'
import Logo from './components/Logo'
import Message from './components/Message'
import Slogan from './components/Slogan'
import VideoBackground from './components/VideoBackground'

function App() {

  return (
      <>
      <VideoBackground />
      <div className={"overlay-div container"}>
        <Logo />
        <Slogan />
        <Message />
        <Footer />
      </div>
      </>
  )
}

export default App
