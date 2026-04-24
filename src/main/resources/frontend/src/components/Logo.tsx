import pmtvLogo from '../assets/tvlogo-whiteout2.svg';

const Logo = () => (
      <div className="relative flex flex-col top-5">
        <img
          src={pmtvLogo}
          alt="Paymetv"
          className="landing-logo-default landing-logo-sm landing-logo-md landing-logo-lg"
        />
    </div>
  );

export default Logo;


