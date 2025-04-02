import tweepy

# Replace these values with your own API keys and access tokens
API_KEY = '57lZvqwFwuJlZVVWwJpdjImEH'
API_SECRET_KEY = 'ZPXLwCLBkmvVITRkv69LDloEkuAtTErqb07xGTu5prfKujtBQb'
ACCESS_TOKEN = '1856671548448395264-8YuvArGFHb2ESyejlZwMt99w8AONci'
ACCESS_TOKEN_SECRET = 'CeG5aRW340y7s8P0K3fowEunv5U7AG2VdwPHzfr6ATL3v'

# Authenticate to Twitter
auth = tweepy.OAuth1UserHandler(API_KEY, API_SECRET_KEY, ACCESS_TOKEN, ACCESS_TOKEN_SECRET)
api = tweepy.API(auth)

# Define the hashtag to search for
hashtag = "#BMWGroup"

# Define search query and number of tweets
query = 'python'
num_tweets = 10

# Extract tweets
tweets = tweepy.Cursor(api.search_tweets, q=hashtag).items(num_tweets)

# Print tweets
for tweet in tweets:
    print(tweet.text)