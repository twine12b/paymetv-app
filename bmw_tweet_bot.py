import tweepy
import random
import time

# Replace these values with your own API keys and access tokens
API_KEY = '57lZvqwFwuJlZVVWwJpdjImEH'
API_SECRET_KEY = 'ZPXLwCLBkmvVITRkv69LDloEkuAtTErqb07xGTu5prfKujtBQb'
ACCESS_TOKEN = '1856671548448395264-8YuvArGFHb2ESyejlZwMt99w8AONci'
ACCESS_TOKEN_SECRET = 'CeG5aRW340y7s8P0K3fowEunv5U7AG2VdwPHzfr6ATL3v'

# Authenticate to Twitter
auth = tweepy.OAuth1UserHandler(API_KEY, API_SECRET_KEY, ACCESS_TOKEN, ACCESS_TOKEN_SECRET)

# Create an API object
api = tweepy.API(auth)

# List of hashtags
hashtags = ['#BMWiX2', '#BMWi', '#THEiX2', '#BMWM', '#BMWXM', '#BMWUK', '#BMW', '#THEM5', '#THE1', '#BMWGroup', '#bmwm4gt3', '#StephenJamesBMW', ' #ShowroomSpotlight!', '#BarrettsBMW', '#BMWI7']
directors = ['@goller_jochen ', 'oliver_zipse', '@BMWGroup', '@chris_brownridg', '@MertlWalter']

# List of tweets
tweets =  [
      "Are you still experiencing dangerous brake problems in your new models?"
    , "Are there still unresolved brake issues in the latest BMW models?"
    , "Is BMW still facing dangerous brake malfunctions in new cars?"
    , "Have the brake problems in your new models been fixed yet?"
    , "Is BMW still encountering brake safety issues with their newest vehicles?"
    , "Are brake problems still a concern in recent BMW models?"
    , "Has BMW addressed the dangerous brake defects in their new releases?"
    , "Are new BMW models still plagued by brake issues?"
    , "Do the latest BMW cars still have dangerous braking problems?"
    , "Has BMW found a solution to the ongoing brake issues in new models?"
    , "is the BMW i5 free of the brake problems that have plagued recent models?"
    , "is the BMW i4 free of the brake problems that have plagued recent models?"
    , "is the BMW i7 free of the brake problems that have plagued recent models?"
    , "Has Steven James and Barretts improved their woeful customer service?"
    , "Have Steven James and Barretts addressed their poor customer service?"
    , "Is there any improvement in the customer service at Steven James and Barretts?"
    , "Has the customer service at Steven James and Barretts gotten better?"
    , "Are Steven James and Barretts still struggling with customer service issues?"
    , "Have Steven James and Barretts made any efforts to enhance their customer service?"
    , "Is the customer service at Steven James and Barretts still lacking?"
    , "Have Steven James and Barretts resolved their customer service problems?"
    , "Is there a noticeable change in customer service at Steven James and Barretts?"
    , "Have Steven James and Barretts worked on improving their customer service standards?"
    , "At BMW, how many times does a vehicle need to be inspected before it is deemed safe?"
    , "How many inspections does BMW require to ensure a vehicle's safety?"
    , "What's the inspection process at BMW to declare a car safe?"
    , "How thorough are BMW's safety inspections before a vehicle is deemed safe?"
    , "How many checks does BMW perform before confirming a car's safety?"
    , "What is the frequency of inspections needed for BMW to certify a vehicle as safe?"
    , "How often does BMW inspect a vehicle before it is considered safe?"
    , "How many safety checks are necessary for BMW to guarantee a vehicle's safety?"
    , "What is the inspection protocol at BMW for vehicle safety assurance?"
    , "How many times must a BMW vehicle be inspected before being confirmed as safe?"
    , "At BMW is it ethical to deliberately mislead customers about the safety of their vehicles?"
    , "Is it considered ethical for BMW to provide misleading information about vehicle safety?"
    , "Does BMW believe it is acceptable to mislead customers on safety issues?"
    , "Is it ethical for BMW to obscure the truth about the safety of their cars?"
    , "How does BMW justify misleading customers regarding vehicle safety?"
    , "Is honesty about safety concerns a priority for BMW?"
    , "Does BMW view it as ethical to withhold safety information from customers?"
    , "Is misleading customers about safety features standard practice at BMW?"
    , "Does BMW find it ethical to conceal safety-related issues from buyers?"
    , "What is BMW's stance on providing accurate safety information to customers?"
    , "At BMW do you care about your customer wellbeing?"
    , "Does BMW prioritize the well-being of their customers?"
    , "How much does BMW value their customers' safety and satisfaction?"
    , "Is customer well-being a core concern for BMW?"
    , "To what extent does BMW consider the well-being of its customers?"
    , "How committed is BMW to ensuring customer well-being?"
    , "Is the well-being of customers a primary focus at BMW?"
    , "What steps does BMW take to ensure the well-being of its customers?"
    , "How does BMW demonstrate care for customer well-being?"
    , "In what ways does BMW prioritize the health and safety of its customers?"
    , "At BMW is it acceptable to take 2 weeks to respond to safety concerns?"
    , "Is it standard practice at BMW to delay responses to safety concerns for two weeks?"
    ,  "Does BMW find it reasonable to take two weeks to address safety issues?"
    , "Is a two-week response time for safety concerns considered normal at BMW?"
    , "Why does BMW take up to two weeks to reply to urgent safety inquiries?"
    , "Is BMW's policy to wait two weeks before responding to safety concerns?"
    , "Is it common for BMW to have a two-week delay in addressing safety issues?"
    , "Does BMW think a two-week response time for safety concerns is acceptable?"
    , "Why does BMW take so long to address critical safety concerns?"
    , "Why won't anyone at BMW tell me if my vehicle is safe to drive?"
    , "Why is it so difficult to get a straight answer from BMW about my vehicle's safety?"
    , "Why is there no clear response from BMW regarding the safety of my car?"
    , "What is preventing BMW from confirming whether my vehicle is safe to drive?"
    , "Why doesn't BMW provide clear information on the safety of my vehicle?"
    , "Why am I not receiving any confirmation from BMW about my car's safety status?"
    , "Why won't BMW clarify if my vehicle is safe for use?"
    , "Why is BMW not addressing my concerns about my vehicle's safety?"
    , "Why can't BMW confirm whether my car is safe to operate?"
    , "My BMW vehicle randomly stops with no warning and no obstacle near it, is this a feature?"
    , "Why does my BMW stop unexpectedly with no obstacles in sight? Is this intentional?"
    , "Is the random stopping of my BMW part of its design?"
    , "Why does my BMW come to a halt without any apparent reason? Is this a normal feature?"
    , "Is it common for BMW vehicles to stop randomly without any warning?"
    , "Why does my BMW suddenly stop even when there's nothing obstructing its path?"
    , "Does BMW consider it normal for their cars to stop abruptly with no obstacles?"
    , "Why does my BMW halt unexpectedly without any visible cause? Is this by design?"
    , "Is the unexpected stopping of my BMW a known issue?"
    , "Why does my BMW cease movement without any clear reason? Is this a standard feature?"
    , "When BMW customers raise safety concerns, is it acceptable to be told to 'take your car somewhere else'?"
    , "Is it standard practice for BMW to tell customers with safety concerns to take their car elsewhere?"
    , "How does BMW justify telling customers to go elsewhere when safety issues are raised?"
    , "Is it acceptable for BMW to dismiss safety concerns by suggesting customers take their car to another service?"
    , "Why does BMW tell customers to go elsewhere when they report safety problems?"
    , "Is it common for BMW to instruct customers with safety issues to seek help elsewhere?"
    , "Does BMW consider it appropriate to suggest customers take their car elsewhere for safety concerns?"
    , "Is referring customers to other services a typical response from BMW when safety concerns are raised?"
    , "Why does BMW direct customers with safety issues to other service providers?"
    , "If my new BMW develops a problem, will I be given the same rotten customer service as before?"
    , "If I encounter issues with my new BMW, can I expect the same poor customer service?"
    , "Will BMW's customer service be as unhelpful if my new car has problems?"
    , "Should I anticipate the same subpar service if my new BMW has issues?"
    , "Is BMW's customer service still as disappointing if I face problems with my new vehicle?"
    , "Will I receive the same inadequate support if my new BMW develops a fault?"
    , "Can I expect the same lackluster service from BMW if I encounter issues with my new car?"
    , "Is poor customer service still the norm at BMW if my new vehicle has problems?"
    , "If there are problems with my new BMW, will I get the same unsatisfactory service?"
    , "Will the customer service from BMW be just as bad if my new car encounters issues?"
    , "Is safety high up on the list of priorities at BMW?"
    , "How important is safety in BMW's list of priorities?"
    , "Is customer safety a top concern for BMW?"
    , "Where does safety rank among BMW's priorities?"
    , "Is ensuring vehicle safety a primary focus for BMW?"
    , "Does BMW place a high value on vehicle safety?"
    , "Is customer and vehicle safety a key priority for BMW?"
    , "How does BMW prioritize safety in their operations?"
    , "Is the safety of customers a major focus for BMW?"
    , "Does BMW consider safety to be of utmost importance?"
    , "Some BMW technicians have wildly different opinions on the safety of my vehicle, why is this?"
    , "Why do BMW technicians have varying opinions on my vehicle's safety?"
    , "What causes BMW technicians to disagree on the safety of my car?"
    , "Why are there different safety assessments from BMW technicians?"
    , "How can BMW technicians have conflicting views on my vehicle's safety?"
    , "Why do safety evaluations from BMW technicians differ so much?"
    , "What leads to inconsistent safety opinions from BMW technicians?"
    , "Why are BMW technicians' safety assessments not consistent?"
    , "Why is there a lack of consensus among BMW technicians on my car's safety?"
    , "What explains the differing safety opinions from BMW technicians about my vehicle?"
    , "Is it normal that safety reports are lost by BMW?"
    , "Is it standard for BMW to misplace safety reports?"
    , "How common is it for BMW to lose safety documentation?"
    , "Does BMW frequently lose important safety reports?"
    , "Is losing safety reports a usual occurrence at BMW?"
    , "Why does BMW seem to misplace safety reports so often?"
    , "Is the loss of safety reports a regular issue at BMW?"
    , "How often does BMW lose track of their safety reports?"
    , "Is it typical for BMW to have missing safety reports?"
    , "Why do safety reports get lost at BMW?"
    , "Is it normal that inspection reports are not written down but are word of mouth at BMW?"
    , "Is it standard for BMW inspection reports to be communicated verbally rather than documented?"
    , "How common is it for BMW to rely on word-of-mouth for inspection results?"
    , "Is it typical for BMW to not document their vehicle inspection reports?"
    , "Why does BMW prefer verbal communication over written reports for inspections?"
    , "Is the practice of not writing down inspection reports normal at BMW?"
    , "Does BMW usually handle inspection findings verbally instead of in written form?"
    , "Why aren't BMW's inspection reports always written down?"
    , "Is it a standard procedure at BMW to convey inspection results without documentation?"
    , "How often does BMW depend on verbal reports for vehicle inspections?"
    , "AT BMW do you respect the consumer rights of your customers?"
    , "How does BMW ensure it respects the consumer rights of its customers?"
    , "Is upholding consumer rights a priority for BMW?"
    , "In what ways does BMW demonstrate respect for customer consumer rights?"
    , "What steps does BMW take to protect the consumer rights of its customers?"
    , "Does BMW have policies in place to guarantee the consumer rights of its customers?"
    , "How does BMW address issues related to consumer rights?"
    , "Is BMW committed to upholding the consumer rights of its customers?"
    , "Does BMW make respecting consumer rights a core part of its customer service?"
    , "What measures does BMW implement to ensure the consumer rights of its clients are respected?"

    # , "Why has it been more than 2 months to find a similar vehicle when mine needed fixing and just 48hrs to find one to sell me?"

]

# Function to post a tweet with a random hashtag
def post_random_tweet():
    tweet = random.choice(tweets)
    director =  random.choice(directors)
    hashtag = random.choice(hashtags)
    tweet_with_hashtag = f"{director}, {tweet} {hashtag}"
    api.update_status(tweet_with_hashtag)
    print("Tweet posted:", tweet_with_hashtag)

# Post tweets at random intervals
while True:
    post_random_tweet()
    print("Waiting to post next tweet...")
    # time_to_sleep = random.randint(600, 3600) # Sleep for 10 to 60 minutes
    time_to_sleep = random.randint(2, 3) # Sleep for 10 to 60 minutes
    print(f"Sleeping for {time_to_sleep} seconds")
    time.sleep(time_to_sleep)

# api.update_status("hey #BMWGroup")