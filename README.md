# iuMetro

![logo-header.png](docs/logo-header.png)

IuMetro is a Java application that implements a versatile ticketing solution for a metro transportation system. This
system allows metro operators to construct tickets with time constraints, usage constraints, or no constraints,
providing flexibility for various ticketing options such as day passes, single-use tickets, and more. Additionally, the
system supports a card-based payment option with rechargeable cards that can be charged at ticket machines.

## Features

- Flexible Ticket Generation: Create tickets with various constraints to cater to passenger needs, such as day passes,
  single-use tickets, and custom constraints.

- Card-Based Payment Option: Seamlessly utilize rechargeable cards for hassle-free ticketing, offering users a
  convenient payment alternative.

- Card Recharge Functionality: Easily recharge the metro card at ticket machines to maintain sufficient balance for
  smooth travel experiences.

- Ticket Validation at Barriers: Tickets are validated at ticket barriers, granting access when the ticket is deemed
  valid.

## Getting Started

Follow these steps to set up iuMetro on your local machine:

1. Clone the repository

```
git clone https://github.com/sotterbeck/iu-metro.git
```

2. Navigate to the repository or open it in your favorite IDE

```
cd iu-metro
```

3. Build the Java application using Gradle (make sure you
   have [Java 17](https://adoptium.net/de/temurin/releases/?version=17) installed!)

```
./gradlew build
```

Note: The project is designed with a clean architecture, enabling flexibility for adaptation to various technologies
such as a
Minecraft plugin, RESTful web API, or Discord bot.

## License

This project is licensed under he [Apache-2.0](LICENSE) license.