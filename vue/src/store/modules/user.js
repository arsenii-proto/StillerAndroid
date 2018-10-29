import { Stiller } from "../../services/StillerService";
import state from "../index";

if (Stiller) {
  Stiller.firebase.auth.onLogginStateChanged.on(({ logged }) => {
    state.commit("user/setAuthState", logged);
  });
}

export default {
  namespaced: true,
  state: {
    logged: Stiller && Stiller.firebase.auth.logged
  },
  mutations: {
    setAuthState(state, logged) {
      state.logged = logged;
    }
  },
  actions: {}
};
