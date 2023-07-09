package de.vatrascell.nezr.application.controller;

import com.sothawo.mapjfx.Coordinate;
import org.springframework.stereotype.Controller;

@Controller
public class LocationLogoController {

    public String getLocationLogoPath(String location) {
        String value;
        switch (location) {
            case "Rügen":
                value = "images/svg/logo-naturerbe-zentrum-ruegen.svg";
                break;
            case "Bayerischer Wald":
                value = "images/svg/logo-baumwipfelpfad-bayerische-wald.svg";
                break;
            case "Saarschleife":
                value = "images/svg/logo-baumwipfelpfad-saarschleife.svg";
                break;
            case "Schwarzwald":
                value = "images/svg/logo-baumwipfelpfad-schwarzwald.svg";
                break;
            case "Usedom":
                value = "images/svg/logo-baumwipfelpfad-usedom.svg";
                break;
            case "Elsass":
                value = "images/svg/logo-baumwipfelpfad-elsass.svg";
                break;
            case "Salzkammergut":
                value = "images/svg/logo-baumwipfelpfad-salzkammergut.svg";
                break;
            default: //Bachledka, Krkonoše, Lipno, Pohorje
                value = "images/svg/baumwipfelpfade-logo.svg";
                break;
        }

        return value;
    }

    //TODO add coords to locations
    public Coordinate getLocationsCoordinates(String location) {
        Coordinate value;
        switch (location) {
            case "Rügen":
                value = new Coordinate(54.4298063, 13.5620475);
                break;
            case "Bayerischer Wald":
                value = new Coordinate(48.8908762, 13.4824897);
                break;
            case "Saarschleife":
                value = new Coordinate(49.5021211, 6.5380415);
                break;
            case "Schwarzwald":
                value = new Coordinate(48.750353, 8.5335319);
                break;
            case "Usedom":
                value = new Coordinate(53.9491372, 14.1664812);
                break;
            case "Elsass":
                value = new Coordinate(48.9989052, 7.8546882);
                break;
            case "Bachledka":
                value = new Coordinate(49.2720486, 20.3077584);
                break;
            case "Krkonoše":
                value = new Coordinate(51.0870219, 6.6854701);
                break;
            case "Lipno":
                value = new Coordinate(50.9287899, 6.6848692);
                break;
            case "Pohorje":
                value = new Coordinate(46.4522306, 15.1953542);
                break;
            default:
                value = new Coordinate(54.4298063, 13.5620475);
                break;
        }

        return value;
    }
}
