import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-add-pattern-dialog',
  templateUrl: 'add-pattern-dialog.component.html',
})

export class AddPatternDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<AddPatternDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

}
