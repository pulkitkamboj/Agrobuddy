package com.example.anu.agrobuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Remedy extends AppCompatActivity {
    TextView rem;
    TextView head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remedy);
        rem=findViewById(R.id.textView2);
        head=findViewById(R.id.textView3);
        Intent startingIntent = getIntent();
        String message = startingIntent.getStringExtra("message");
        if(message.equalsIgnoreCase("Yellow Rust of Wheat"))
        {
            head.setText("YELLOW RUST OF WHEAT");
            rem.setText("Casual organism: Puccinia striiformis f sp . tritici\n" +
                    "\n" +
                    "Chief symptoms: Yellow to lemon yellow pustules arranged in narrow stripes or in rows on leaves. \n" +
                    "\n" +
                    "Control measures:\n" +
                    "\n" +
                    "1. Farmers are advised to use the latest resistant varieties recommended for the area.\n" +
                    "\n" +
                    "2. Chemical spray\n" +
                    "\n" +
                    "Foliar spray of Tilt @ 0.1% for the control of brown and yellow rust\n" +
                    "Foliar spray of Plantavax @0.2% for the control of yellow rust.\n");

        }
        else if(message.equalsIgnoreCase("Downy Mildew of Mustard"))
        {
            head.setText("DOWNY MILDEW OF MUSTARD");
            rem.setText("Causal Organism:  Peronospora viciae (formerly P. pisi),\n" +
                    "\n" +
                    "It is caused by a soil borne fungal pathogen.\n" +
                    "The pathogen does not transmitted by seed internally or externally.\n" +
                    "\n" +
                    "Symptoms:\n" +
                    "\n" +
                    "A grayish white moldy growth appears on the lower leaf surface, and a yellowish area on leaf.\n" +
                    "Infected leaves can turn yellow and die if weather is cool and damp.\n" +
                    "Stems may be distorted and stunted.\n" +
                    "Brown blotches appear on pods, and mold may grow inside pods.\n" +
                    "\n" +
                    "Control:\n" +
                    "\n" +
                    "Grow resistant or tolerant cultivars.\n" +
                    "Follow 2-3 years of crop rotation.\n" +
                    "Burn or bale infested pea straw after harvest\n" +
                    "Spray of Mancozeb @ 0.25% at seven days interval gives good control. ");
        }
        else if(message.equalsIgnoreCase("Powdery Mildew of Mustard"))
        {
            head.setText("POWDERY MILDEW OF MUSTARD");
            rem.setText("Causal Organism: Erysiphe graminis tritici\n" +
                    "\n" +
                    "Symptoms\n" +
                    "\n" +
                    "The first visible symptoms are white to pale gray, fuzzy or powdery colonies of mycelia and conidia on the upper leaf surface.\n" +
                    "These patches later turn brownish studded with dot like black structure.\n" +
                    "The fungus usually develops on upper surface of the leaves, which get crinkled, twisted and deformed.\n" +
                    "\n" +
                    "Control Measure\n" +
                    "\n" +
                    "Use wheat varieties for that particular zone.\n" +
                    "Grow resistant varieties.\n" +
                    "Spray karathane 80WP at the rate of 1kg/ha or Bayleton at the rate of 500g/ha in 1000 litres of water at the appearance of first symptoms.");
        }
        else if(message.equalsIgnoreCase("White Rust of Mustard"))
        {
            head.setText("WHITE RUST OF MUSTARD");
            rem.setText("Causal Organism: Albugo candida\n" +
                    "\n" +
                    "Symptoms:\n" +
                    "\n" +
                    "This disease can be a serious menace if it occurs along with Downy Mildew.\n" +
                    "The disease is characterized by the white raised blisters on leaves, stem, petiole and floral parts.\n" +
                    "These blisters burst and liberate a white powder.\n" +
                    "There is also deformity of floral parts. Flowers get malformed and become sterile.\n" +
                    "\n" +
                    "Control measures:\n" +
                    "\n" +
                    "Treat the seed with Apron 35 SD at the rate of 6 g/kg of seed before sowing.\n" +
                    "Spray the crop with 0.2% Ridomil as soon as the symptoms are noticed and repeat the spray if needed at 10 days interval.\n" +
                    "Keep the field free from weeds.");
        }
        else if(message.equalsIgnoreCase("Altenaria blight of tomato"))
        {
            head.setText("ALTENARIA BLIGHT OF TOMATO");
            rem.setText("General\n" +
                    "\n" +
                    "Caused by Alternaria alternata/ A.tenuissima.\n" +
                    "Cause problems in late-sown crops.\n" +
                    "Sporulates well under warm, humid conditions.\n" +
                    "\n" +
                    "Symptoms\n" +
                    "\n" +
                    "Small, circular, necrotic spots on leavesforming typical concentric rings.\n" +
                    "The lesions appear on all aerial plant parts including pods.\n" +
                    "Blighting of leaves and severe defoliation and drying of infected branches.\n" +
                    "\n" +
                    "Management\n" +
                    "\n" +
                    "Select fields away from perennial pigeonpeas.\n" +
                    "Select seed from healthy crops.\n" +
                    "Maneb 3g per liter of water is effective.");

        }

    }

}
