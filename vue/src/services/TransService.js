import { list_of_trans } from "../translate";

const trans = {
  get(path, notInited) {
    let current = list_of_trans[trans.lang];

    current &&
      (`${path}`.split(".") || [`${path}`]).forEach(el => {
        if (current && el in current) {
          current = current[el];
        } else {
          current = undefined;
        }
      });

    return current || notInited || `@trans(${path})`;
  },

  lang: "ru"
};

export default {
  install: function(Vue) {
    trans.lang = Vue.prototype.$stiller.locale.split("_")[0];

    Vue.prototype.trans = (...args) => trans.get.apply(null, args);
  }
};
