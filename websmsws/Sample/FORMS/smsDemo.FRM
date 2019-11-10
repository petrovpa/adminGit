object NewForm: TWForm
  Left = 0
  Top = 0
  Width = 480
  Height = 450
  BgColor = '#FFFFFF'
  FgColor = '#000000'
  object WLabel1: TWLabel
    Left = 0
    Top = 24
    Width = 145
    Height = 17
    Caption = #1058#1077#1082#1089#1090' '#1089#1086#1086#1073#1097#1077#1085#1080#1103
  end
  object WLabel2: TWLabel
    Left = 0
    Top = 256
    Width = 145
    Height = 17
    Caption = #1057#1090#1072#1090#1091#1089' '#1089#1086#1086#1073#1097#1077#1085#1080#1103
  end
  object WLabel3: TWLabel
    Left = 0
    Top = 184
    Width = 121
    Height = 17
    Caption = #1053#1086#1084#1077#1088' '#1058#1077#1083#1077#1092#1086#1085#1072
  end
  object WMemo1: TWMemo
    Left = 0
    Top = 40
    Width = 457
    Height = 121
    TabIndex = 0
    Lines.Strings = (
      '')
    Required = False
  end
  object WButton1: TWButton
    Left = 0
    Top = 328
    Width = 75
    Height = 25
    TabIndex = 0
    Caption = #1054#1090#1087#1088#1072#1074#1080#1090#1100
    JSOnClick.Strings = (
      'sendForm("NEXT", true);')
  end
  object WMemo2: TWMemo
    Left = 0
    Top = 280
    Width = 457
    Height = 25
    TabIndex = 0
    Enabled = False
    Lines.Strings = (
      '')
    Required = False
  end
  object WEdit1: TWEdit
    Left = 0
    Top = 200
    Width = 121
    Height = 18
    PasswordField = False
    TabIndex = 0
    Required = False
    Alignment = taLeftJustify
    MinLength = 0
    InputKind = dfNone
  end
  object WDBCards: TWDBTable
    Left = 8
    Top = 360
    Width = 460
    Height = 78
    BorderStyle = bsSingle
    BgColor = '#F0F0F0'
    FgColor = '#000000'
    MultiSelect = False
    RowsCount = 10
    OperationMode = tdoNone
    TableColumnsDescription.Strings = (
      #1057#1090#1072#1090#1091#1089';status;;SHOW;STRING;CENTER;;'
      #1053#1086#1084#1077#1088' '#1082#1072#1088#1090#1099';cardnumber;;SHOW;STRING;CENTER;;')
    ServiceName = 'websmsws'
    ServiceMethod = 'getCard'
  end
end
