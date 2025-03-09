FROM ubuntu:latest
LABEL authors="Benjamin Lampart"

ENTRYPOINT ["top", "-b"]