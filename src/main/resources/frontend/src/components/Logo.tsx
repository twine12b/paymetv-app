import { ReactNode } from "react";
import pmtvLogo from '../assets/tvlogo-whiteout2.svg';

const Logo = () => {
  return (
      <div className="relative flex flex-col top-5">
        <img
          src={pmtvLogo}
          alt="Paymetv"
          className="w-full max-w-[180px] sm:max-w-[220px] md:max-w-[320px] lg:max-w-[350px] xl:max-w-[450px] h-auto block"
        />
    </div>
  );
};

export default Logo;


