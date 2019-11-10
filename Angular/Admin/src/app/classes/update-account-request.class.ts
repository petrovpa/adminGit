import { AccountRequest } from './account-request.class';

export class UpdateAccountRequest extends AccountRequest {
    constructor(data: any) {
        super();
        Object.assign(this, data);
    }
}