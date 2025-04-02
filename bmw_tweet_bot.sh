#!/bin/bash

# list of tweets
tweets=(
	"Exciting news from the world of BMW!"
  "Check out the latest BMW models!"
  "Discover the thrill of driving a BMW!"
  "Experience the luxury of BMW today!"
			)

# List of hashtags
testing="-testing-RRR"
   hashtags=(
    "#bmw-uk$testing"
    "#bmw-sales$testing"
    "#bmw-news$testing"
    "#bmw-life$testing"


#	   "#bmw-uk"
#     "#bmw-sales"
#		 "#bmw-news"
#		 "#bmw-life"
	)

# Function to post a tweet with a random hashtag
post_random_tweet() {
  tweet=${tweets[$RANDOM % ${#tweets[@]}]}
  hashtag=${hashtags[$RANDOM % ${#hashtags[@]}]}
  tweet_with_hashtag="$tweet $hashtag"
  t update "$tweet_with_hashtag"
  echo "Tweet posted: $tweet_with_hashtag"
}

# Post tweets at random intervals
while true; do
  post_random_tweet sleep $((RANDOM % 3600 + 600))
  done
