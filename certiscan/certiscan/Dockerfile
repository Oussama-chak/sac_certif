# Use official Node.js image
FROM node:18

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install

# Copy the entire Angular project
COPY . .

EXPOSE 4100

# Start Angular development server
CMD ["npm", "run", "start", "--", "--host", "0.0.0.0"]
