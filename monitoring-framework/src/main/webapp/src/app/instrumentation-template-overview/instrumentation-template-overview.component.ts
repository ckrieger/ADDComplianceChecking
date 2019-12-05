import { Component, OnInit } from '@angular/core';
import { TemplateService } from '../services/template.service';

@Component({
  selector: 'app-instrumentation-template-overview',
  templateUrl: './instrumentation-template-overview.component.html',
  styleUrls: ['./instrumentation-template-overview.component.css']
})
export class InstrumentationTemplateOverviewComponent implements OnInit {
  templates: Array<any>;
  language = "typescript";
  sampleContent = `
  <pre>
     <code class="typescript highlight">
         class Greeter {
             constructor(public greeting: string) { }
             greet() {
                 return "hello world";
             }
         };
     </code>
 </pre>
 <pre>
     <code class="javascript highlight">
         alert('Hello, World!');
     </code>
 </pre>
 `;

  constructor(private templateService: TemplateService) { }

  ngOnInit() {
   this.fetchAllTemplates();
  }

  remove(template) {
    this.templateService.remove(template.id).subscribe(result => {
      let index = this.templates.findIndex(templateIn => templateIn.id === template.id);
      this.fetchAllTemplates();
    }, error => console.error(error));
  }

  private fetchAllTemplates() {
    this.templateService.getAll().subscribe(data => {
      this.templates = data;
    })
  }

}
