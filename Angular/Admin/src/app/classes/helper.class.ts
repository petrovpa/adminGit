import * as _ from 'lodash';

export class Helper {

    /**
     * Получение поля объекта с проверкой на наличие родителя
     */
    static get = (p, o) =>
        p.reduce((xs, x) => (xs && xs[x]) ? xs[x] : null, o);

    /**
     * Приведение ключей объектов к нижнему регистру
     * @param data {any} - приводимый объект
     */
    static convertToLowCaseObjectKey = (data: any) =>
        _.mapKeys(data, (v: any, k: string) => k.toLowerCase());

    /**
     * Рекурсивное приведение ключей объекта и всех его вложенных объектов к нижнему регистру
     * @param data {any} - приводимый объект
     * @returns - объект или массив (в зависимости от типа входных данных)
     */
    static recursiveToLowCaseObjectKey = (value) => {
        if (_.isPlainObject(value)) {
            return Helper.mapObject(value);
        }
        if (_.isArray(value)) {
            return Helper.mapArray(value);
        }
    }

    /**
     * Приведение ключей объектов к нижнему регистру в зависимости от типа входных данных
     */
    static convertObjectToLowCase = (value) => {
        if (_.isPlainObject(value) || _.isArray(value)) {
            const data = _.isPlainObject(value) ? Helper.convertToLowCaseObjectKey(value) : Helper.mapArray(value);
            return Helper.recursiveToLowCaseObjectKey(data);
        } else {
            return value;
        }
    }

    static mapObject = (data) =>
        _.mapValues(data, (value: any) => Helper.convertObjectToLowCase(value));

    static mapArray = (data) =>
        _.map(data, (value: any) => Helper.convertObjectToLowCase(value));

}