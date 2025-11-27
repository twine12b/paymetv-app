import vidBack from '../assets/backvideo2.mp4';

const VideoBackground = () => {
    return (
            <video
                src={vidBack}
                autoPlay
                loop
                muted
                playsInline
                className="landing-video"
            />
    );
};

export default VideoBackground;