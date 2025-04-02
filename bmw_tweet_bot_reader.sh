#!/bin/bash

# Replace these values with your own API keys and access tokens
API_KEY='57lZvqwFwuJlZVVWwJpdjImEH'
API_SECRET_KEY='ZPXLwCLBkmvVITRkv69LDloEkuAtTErqb07xGTu5prfKujtBQb'
ACCESS_TOKEN='1856671548448395264-8YuvArGFHb2ESyejlZwMt99w8AONci'
ACCESS_TOKEN_SECRET='CeG5aRW340y7s8P0K3fowEunv5U7AG2VdwPHzfr6ATL3v'

# Define the hashtag to search for
hashtag="#BMWGroup"

# Define number of tweets
num_tweets=10

# Encode credentials
credentials=$(echo -n "$API_KEY:$API_SECRET_KEY" | base64)

echo "starting...."
# Get bearer token
response=$(curl -s -X POST "https://api.twitter.com/oauth2/token" \
  -H "Authorization: Basic $credentials" \
  -H "Content-Type: application/x-www-form-urlencoded;charset=UTF-8" \
  -d "grant_type=client_credentials")

bearer_token=$(echo $response | jq -r '.access_token')

# Search for tweets
tweets=$(curl -s -X GET "https://api.twitter.com/1.1/search/tweets.json?q=$hashtag&count=$num_tweets" \
  -H "Authorization: Bearer $bearer_token")

# Print tweets
echo $tweets | jq -r '.statuses[] | .text'