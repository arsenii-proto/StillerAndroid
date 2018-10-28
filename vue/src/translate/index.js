import { en_login } from "./en/login";
import { en_welcome } from "./en/welcome";

import { ru_login } from "./ru/login";
import { ru_welcome } from "./ru/welcome";
import { ru_client_map } from "./ru/client_map";


export const list_of_trans = {

    ru: {

        login: ru_login,
        welcome: ru_welcome,
        client_map: ru_client_map,
    },

    en: {

        login: en_login,
        welcome: en_welcome,
    }
}