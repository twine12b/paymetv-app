import vidBack from '../assets/backvideo2.mp4';

const VideoBackground = () => {
    return (
            <video
                src={vidBack}
                autoPlay
                loop
                muted
                playsInline
                className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full h-full object-contain -z-10"
            />
    );
};

export default VideoBackground;