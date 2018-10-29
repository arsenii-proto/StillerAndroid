import { Stiller } from "../../services/StillerService";

export default {
  namespaced: true,
  state: {
    countryCode: "+373",
    phoneNumber: "68 50 56 35",
    phoneNumberValid: false,
    codeSended: false,
    codeSMS: "",
    codeSMSValid: false
  },
  mutations: {
    setNumber(state, number) {
      state.phoneNumber = number;

      state.phoneNumberValid = /^\d\d\s\d\d\s\d\d\s\d\d\s?$/.test(number);
    },
    codeSended(state) {
      state.codeSended = true;
    }
  },
  actions: {
    numberChanged({ commit }, data) {
      console.log(data);
      if (/^(\d?\d?\s?){0,4}$/.test(data.number)) {
        commit(
          "setNumber",
          String(data.number)
            .replace(
              /^(\d?\d?)\s?(\d?\d?)\s?(\d?\d?)\s?(\d?\d?)\s?$/g,
              "$1 $2 $3 $4"
            )
            .trim()
        );
      }
    },
    sendNumber({
      commit,
      state: {
        loginState: { countryCode, phoneNumber }
      }
    }) {
      if (Stiller) {
        Stiller.firebase.auth
          .signInWithPhoneNumber(
            `${countryCode}${(phoneNumber.replace(/\s/g), "")}`
          )
          .then(() => {
            commit("codeSended");
          });
      }
    }
  }
};
