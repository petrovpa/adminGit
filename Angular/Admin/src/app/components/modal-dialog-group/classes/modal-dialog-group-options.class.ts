import { Mappable } from '@core/services/common';

export class GroupModalDialogOptions extends Mappable {
    private _modalDialogConfig: string;
    private _modalDialogTitle: string;
    private _labelTitle: string;
    private _onClickAddButton: Function;

    constructor(
        modalDialogTitle: string = '',
        labelTitle: string = '') {

        super();
        this._modalDialogTitle = modalDialogTitle;
        this._labelTitle = labelTitle;
    }

    public get modalDialogConfig(): string {
        return this._modalDialogConfig;
    }

    public set modalDialogConfig(value: string) {
        this._modalDialogConfig = value;
    }

    public get modalDialogTitle(): string {
        return this._modalDialogTitle;
    }

    public get labelTitle(): string {
        return this._labelTitle;
    }

    public set labelTitle(value: string) {
        this._labelTitle = value;
    }

    public get onClickConfirmButton(): Function {
        return this._onClickAddButton;
    }

    public set onClickConfirmButton(value: Function) {
        this._onClickAddButton = value;
    }

    public get onClickAddButton(): Function {
        return this._onClickAddButton;
    }

    public set onClickAddButton(value: Function) {
        this._onClickAddButton = value;
    }

}
