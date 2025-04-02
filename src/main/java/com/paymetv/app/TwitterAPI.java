import com.twitter.clientlib.ApiClient;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.Configuration;
import com.twitter.clientlib.auth.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.TweetCreateRequest;
import com.twitter.clientlib.model.TweetCreateResponse;

import java.util.*;
public class TwitterAPI {
        private static final String BEARER_TOKEN = "YOUR_BEARER_TOKEN";
        private static final List<String> HASHTAGS = Arrays.asList("#BMWiX2", "#BMWi", "#THEiX2", "#BMWM", "#BMWXM", "#BMWUK", "#BMW", "#THEM5", "#THE1", "#BMWGroup", "#bmwm4gt3", "#StephenJamesBMW", "#ShowroomSpotlight!", "#BarrettsBMW", "#BMWI7");
        private static final List<String> DIRECTORS = Arrays.asList("@goller_jochen", "oliver_zipse", "@BMWGroup", "@chris_brownridg", "@MertlWalter");
        private static final List<String> TWEETS = Arrays.asList(
                "Are you still experiencing dangerous brake problems in your new models?",
                "Are there still unresolved brake issues in the latest BMW models?",
                "Is BMW still facing dangerous brake malfunctions in new cars?",
                "Have the brake problems in your new models been fixed yet?",
                "Is BMW still encountering brake safety issues with their newest vehicles?",
                "Are brake problems still a concern in recent BMW models?",
                "Has BMW addressed the dangerous brake defects in their new releases?",
                "Are new BMW models still plagued by brake issues?",
                "Do the latest BMW cars still have dangerous braking problems?",
                "Has BMW found a solution to the ongoing brake issues in new models?"
                // Add more tweets as needed
        );

        public static void main(String[] args) {
                TwitterApi apiInstance = new TwitterApi(new TwitterCredentialsBearer(BEARER_TOKEN));
                Random random = new Random();

                while (true) {
                        try {
                                postRandomTweet(apiInstance, random);
                                System.out.println("Waiting to post next tweet...");
                                int timeToSleep = random.nextInt(2) + 2; // Sleep for 2 to 3 seconds
                                System.out.println("Sleeping for " + timeToSleep + " seconds");
                                TimeUnit.SECONDS.sleep(timeToSleep);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
        }

        private static void postRandomTweet(TwitterApi apiInstance, Random random) throws ApiException {
                String tweet = TWEETS.get(random.nextInt(TWEETS.size()));
                String director = DIRECTORS.get(random.nextInt(DIRECTORS.size()));
                String hashtag = HASHTAGS.get(random.nextInt(HASHTAGS.size()));
                String tweetWithHashtag = director + ", " + tweet + " " + hashtag;

                TweetCreateRequest tweetCreateRequest = new TweetCreateRequest();
                tweetCreateRequest.setText(tweetWithHashtag);

                TweetCreateResponse result = apiInstance.tweets().createTweet(tweetCreateRequest);
                System.out.println("Tweet posted: " + tweetWithHashtag);
        }
}

import com.twitter.clientlib.ApiClient;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.Configuration;
import com.twitter.clientlib.auth.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.TweetCreateRequest;
import com.twitter.clientlib.model.TweetCreateResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TwitterBot {
        private static final String BEARER_TOKEN = "YOUR_BEARER_TOKEN";
        private static final List<String> HASHTAGS = Arrays.asList("#BMWiX2", "#BMWi", "#THEiX2", "#BMWM", "#BMWXM", "#BMWUK", "#BMW", "#THEM5", "#THE1", "#BMWGroup", "#bmwm4gt3", "#StephenJamesBMW", "#ShowroomSpotlight!", "#BarrettsBMW", "#BMWI7");
        private static final List<String> DIRECTORS = Arrays.asList("@goller_jochen", "oliver_zipse", "@BMWGroup", "@chris_brownridg", "@MertlWalter");
        private static final List<String> TWEETS = Arrays.asList(
                "Are you still experiencing dangerous brake problems in your new models?",
                "Are there still unresolved brake issues in the latest BMW models?",
                "Is BMW still facing dangerous brake malfunctions in new cars?",
                "Have the brake problems in your new models been fixed yet?",
                "Is BMW still encountering brake safety issues with their newest vehicles?",
                "Are brake problems still a concern in recent BMW models?",
                "Has BMW addressed the dangerous brake defects in their new releases?",
                "Are new BMW models still plagued by brake issues?",
                "Do the latest BMW cars still have dangerous braking problems?",
                "Has BMW found a solution to the ongoing brake issues in new models?"
                // Add more tweets as needed
        );

        public static void main(String[] args) {
                TwitterApi apiInstance = new TwitterApi(new TwitterCredentialsBearer(BEARER_TOKEN));
                Random random = new Random();

                while (true) {
                        try {
                                postRandomTweet(apiInstance, random);
                                System.out.println("Waiting to post next tweet...");
                                int timeToSleep = random.nextInt(2) + 2; // Sleep for 2 to 3 seconds
                                System.out.println("Sleeping for " + timeToSleep + " seconds");
                                TimeUnit.SECONDS.sleep(timeToSleep);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
        }

        private static void postRandomTweet(TwitterApi apiInstance, Random random) throws ApiException {
                String tweet = TWEETS.get(random.nextInt(TWEETS.size()));
                String director = DIRECTORS.get(random.nextInt(DIRECTORS.size()));
                String hashtag = HASHTAGS.get(random.nextInt(HASHTAGS.size()));
                String tweetWithHashtag = director + ", " + tweet + " " + hashtag;

                TweetCreateRequest tweetCreateRequest = new TweetCreateRequest();
                tweetCreateRequest.setText(tweetWithHashtag);

                TweetCreateResponse result = apiInstance.tweets().createTweet(tweetCreateRequest);
                System.out.println("Tweet posted: " + tweetWithHashtag);
        }
}