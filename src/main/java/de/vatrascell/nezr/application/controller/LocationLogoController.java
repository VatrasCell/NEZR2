package de.vatrascell.nezr.application.controller;

import de.vatrascell.nezr.application.GlobalVars;
import org.springframework.stereotype.Controller;

@Controller
public class LocationLogoController {

    public String getLocationLogoPath(String location) {
        String value;
        switch (GlobalVars.location) {
            case "R\u00FCgen":
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
}
