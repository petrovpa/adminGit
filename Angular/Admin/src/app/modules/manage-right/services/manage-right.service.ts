import { Injectable } from '@angular/core';
import { RestService } from '@core/services/common';
import 'rxjs/add/operator/map';

@Injectable()
export class ManageRightService {

  constructor(private rest: RestService) {
  }

  /**
   * Получения списка доступных для добавления прав
   * @param {number} objId
   * @param {string} rightType
   * @param {string} rightOwner
   * @returns {any | any}
   */
  rightAvailable(objId: number, rightType: string, rightOwner: string) {
    return this.rest.doCall('rights/loadAvailableObjectRights', {
      RIGHTTYPE: rightType,
      OBJECTID: objId,
      RIGHTOWNER: rightOwner
    }).map((res) => {
      if (!res.Error) {
        return res;
      }

      return [];
    });
  }

  /**
   * Добавления простого права
   *
   * @param objId
   * @param rightId
   * @param filterSysName
   * @param filters
   * @param rightType
   * @param rightOwner
   */
  rightAdd(objId: number, rightId: number, filterSysName, filters: any, rightType: string, rightOwner: string) {
    return this.rest.doCall('rights/addAnyRights', {
      OBJECTID: objId,
      RIGHTTYPE: rightType,
      RIGHTID: rightId,
      RIGHTOWNER: rightOwner,
      FILTERSYSNAME: filterSysName ,
      FILTERVALUES: filters
    });
  }

  /**
   * Загрузить список фильтров права
   *
   * @param objId
   * @param rightId
   * @param rightOwner
   * @returns {any | any}
   */
  rightParamLoad(objId: number, rightId: number, rightOwner: string) {
    return this.rest.doCall('rights/loadRightFiltersForObject', {
      OBJECTID: objId,
      RIGHTID: rightId,
      RIGHTOWNER: rightOwner
    });
  }

  /**
   * Загрузить список фильтров права
   *
   * @param {number} objectId
   * @returns {any | any}
   */
  rightMetadataLoad(objectId: number) {
    return this.rest.doCall('rights/loadFilterMetadataForRight', {
      RIGHTID: objectId
    });
  }
}
