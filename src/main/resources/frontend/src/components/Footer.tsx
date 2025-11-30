import { ReactNode } from "react";

const Footer = () => {
  return (
      <div>
        <footer className="absolute bottom-4 sm:bottom-4 md:bottom-6 lg:bottom-8 w-full text-[0.6rem] sm:text-[0.5rem] md:text-[0.7] lg:text-[1.1rem] text-center text-blue-400 px-4 sm:px-6 md:px-8 lg:px-12 md:text-center">
                  <div className="text-center max-w-fill mx-auto leading-tight sm:leading-normal mx-30">
                      Copyright © 2025 PayMeTV enterprises.<br />
                      All rights reserved. Use of this website signifies your agreement to the Terms of Use and Online Privacy Policy (updated 01-07-2025)
                  </div>
        </footer>
    </div>
  );
};

export default Footer;
