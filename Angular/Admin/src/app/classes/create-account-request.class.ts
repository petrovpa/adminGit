import { AccountRequest } from './account-request.class';

export class CreateAccountRequest extends AccountRequest {
    private _RETPASSWORD: string;
    private _BLOCKIFINACTIVE: number;
    private _PWDEXPDATE: string;

    constructor(data: any) {
        super();
        Object.assign(this, data);
    }

    public get RETPASSWORD(): string {
        return this._RETPASSWORD;
    }

    public set RETPASSWORD(value: string) {
        this._RETPASSWORD = value;
    }

    public get BLOCKIFINACTIVE(): number {
        return this._BLOCKIFINACTIVE;
    }

    public set BLOCKIFINACTIVE(value: number) {
        this._BLOCKIFINACTIVE = value;
    }

    public get PWDEXPDATE(): string {
        return this._PWDEXPDATE;
    }

    public set PWDEXPDATE(value: string) {
        this._PWDEXPDATE = value;
    }

}