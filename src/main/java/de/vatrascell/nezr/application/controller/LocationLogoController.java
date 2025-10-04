package de.vatrascell.nezr.application.controller;

import de.vatrascell.nezr.model.Coordinate;
import org.springframework.stereotype.Controller;

@Controller
public class LocationLogoController {

    public String getLocationLogoPath(String location) {

        return switch (location) {
            case "Rügen" -> "images/svg/logo-naturerbe-zentrum-ruegen.svg";
            case "Bayerischer Wald" -> "images/svg/logo-baumwipfelpfad-bayerische-wald.svg";
            case "Saarschleife" -> "images/svg/logo-baumwipfelpfad-saarschleife.svg";
            case "Schwarzwald" -> "images/svg/logo-baumwipfelpfad-schwarzwald.svg";
            case "Usedom" -> "images/svg/logo-baumwipfelpfad-usedom.svg";
            case "Elsass" -> "images/svg/logo-baumwipfelpfad-elsass.svg";
            case "Salzkammergut" -> "images/svg/logo-baumwipfelpfad-salzkammergut.svg";
            default -> //Bachledka, Krkonoše, Lipno, Pohorje
                    "images/svg/baumwipfelpfade-logo.svg";
        };
    }

    //TODO add coords to locations
    public Coordinate getLocationsCoordinates(String location) {

        return switch (location) {
            case "Rügen" -> new Coordinate(54.4298063, 13.5620475);
            case "Bayerischer Wald" -> new Coordinate(48.8908762, 13.4824897);
            case "Saarschleife" -> new Coordinate(49.5021211, 6.5380415);
            case "Schwarzwald" -> new Coordinate(48.750353, 8.5335319);
            case "Usedom" -> new Coordinate(53.9491372, 14.1664812);
            case "Elsass" -> new Coordinate(48.9989052, 7.8546882);
            case "Bachledka" -> new Coordinate(49.2720486, 20.3077584);
            case "Krkonoše" -> new Coordinate(51.0870219, 6.6854701);
            case "Lipno" -> new Coordinate(50.9287899, 6.6848692);
            case "Pohorje" -> new Coordinate(46.4522306, 15.1953542);
            default -> new Coordinate(54.4298063, 13.5620475);
        };
    }
}
