import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormControl, NgForm } from '@angular/forms';
import { TemplateService } from '../services/template.service';
import { MatAutocomplete, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { map, startWith } from 'rxjs/operators';
import { MatChipInputEvent } from '@angular/material/chips';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { PatternService } from '../services/pattern.service';
import { FileUploadService } from '../services/fileUpload.service';

@Component({
  selector: 'app-template-edit',
  templateUrl: './template-edit.component.html',
  styleUrls: ['./template-edit.component.css']
})
export class TemplateEditComponent implements OnInit {

  template: any = {};
  patterns: any[] = [];
  separatorKeysCodes: number[] = [ENTER, COMMA];
  patternCtrl = new FormControl();
  filteredPatterns: Observable<any[]>;
  allPatterns: any[] = [];
  sub: Subscription;
  srcResult;

  fileToUpload: File = null;


  // @ts-ignore
  @ViewChild('patternInput', {static: false}) patternInput: ElementRef<HTMLInputElement>;
  // @ts-ignore
  @ViewChild('auto', {static: false}) matAutocomplete: MatAutocomplete;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private templateService: TemplateService,
    private patternService: PatternService,
    private snackBar: MatSnackBar,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit() {
    this.fetchListOfAllPatterns();
    this.sub = this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.templateService.get(id).subscribe((template: any) => {
          if (template) {
            this.template = template;
            this.fetchListOfLinkedPatterns(template.id);
          } else {
            console.log(`Template with id '${id}' not found, returning to list`);
          }
        });
      }
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  private fetchListOfAllPatterns() {
    this.patternService.getAll().subscribe((patterns: any[]) => {
      this.allPatterns = patterns;
      this.filteredPatterns = this.patternCtrl.valueChanges.pipe(
        startWith(null),
        map((pattern: any | null) => pattern ? this._filter(pattern): this.allPatterns.slice())
      )
    })
  }

  private fetchListOfLinkedPatterns(templateId){
    this.templateService.getAllPatternsOfTemplate(templateId).subscribe((patterns: any) => {
      this.patterns = patterns;
      this.patterns.forEach( pattern => this._filter(pattern));
      this.patternCtrl.setValue(null);
    })
  }

  removePattern(pattern: any[]): void {
    const index = this.patterns.indexOf(pattern);

    if (index >= 0) {
      this.patterns.splice(index, 1);
      this.allPatterns.push(pattern);
      this.patternCtrl.setValue(null);
    }
  }

  gotoList() {
    this.router.navigate(['/template-list']);
  }

  openSnackBar(message: string, action: string, duration: number) {
    this.snackBar.open(message, action, {
      duration: duration,
    });
  }



  save(form: NgForm) {
    if (form.name.trim() != "") {
      this.templateService.add(this.template).subscribe(response => {
        this.savePattensOfTemplate(response.body);
        this.saveFileOfTemplate(response.body);
      }, error => {
        console.error(error);
      });
    } else {
      this.openSnackBar('Template name must not be empty', 'close', 2000)
    }
  }

  private savePattensOfTemplate(template){
    this.templateService.updatePatternsOfTemplate(template.id, this.patterns).subscribe(response => {
      this.gotoList();
    })
  }

  private saveFileOfTemplate(template){
    let formData = new FormData();
    formData.append("file", this.fileToUpload);
    this.templateService.uploadFileOfTemplate(template.id, formData)
      .subscribe((succ) => console.log(succ), err => console.log(err))
  }

  remove(form: NgForm) {
    this.templateService.remove(form).subscribe(result => {
      this.gotoList();
    }, error => console.error(error));
  }

  cancel() {
    this.gotoList();
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    console.log(event.option.value)
    this.patterns.push(event.option.value);
    this.patternInput.nativeElement.value = '';
    this.patternCtrl.setValue(null);
  }

  private _filter(value: any): string[] {
    const index = this.allPatterns.findIndex(pattern => pattern.id == value.id);
    return this.allPatterns.splice(index, 1);
  }

  onFileChanged(event) {
    this.fileToUpload = event.target.files.item(0);
  }

  onFileSelected() {
    const inputNode: any = document.querySelector('#file');

    if (typeof (FileReader) !== 'undefined') {
      const reader = new FileReader();

      reader.onload = (e: any) => {
        this.srcResult = e.target.result;
      };

      reader.readAsArrayBuffer(inputNode.files[0]);
    }
  }
  // addPattern(event: MatChipInputEvent): void {
  //   // Add fruit only when MatAutocomplete is not open
  //   // To make sure this does not conflict with OptionSelected Event
  //   if (!this.matAutocomplete.isOpen) {
  //     const input = event.input;
  //     const value = event.value;
  //    // console.log(event);
  //     // Add our fruit
  //     if (value) {
  //       this.patterns.push(value);
  //     }
  //
  //     // Reset the input value
  //     if (input) {
  //       input.value = '';
  //     }
  //
  //     this.patternCtrl.setValue(null);
  //   }
  // }

}
