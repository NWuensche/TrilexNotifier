# Trilex Delay Instapush Notifier

This is a program which scrapes the web for delays of your train and informs you via Instapush. It uses a `curl` with a bash script.

## How to use
You have to add a class `Secrets` with all the necessary information.

## HTTPComponents instead of curl
You can find the program with `HTTPComponents` instead of `curl` on the other branch.

## How to build a jar
1. `mvn clean package`
2. Look for the .jar in `target` folder.

