set -e

./gradlew clean
./gradlew build
fly deploy
